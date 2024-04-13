package com.honeyapps.wordlebattles.ui.viewmodels

import android.content.Context
import androidx.compose.ui.util.fastJoinToString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.data.models.CellModel
import com.honeyapps.wordlebattles.data.models.KeyModel
import com.honeyapps.wordlebattles.data.models.MatchModel
import com.honeyapps.wordlebattles.data.models.PlayerModel
import com.honeyapps.wordlebattles.utils.KonfettiState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

class MatchViewModel : ViewModel() {

    private val _user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    private var _hasUserPlayed = false
    private var _isMatchCreatedByUser: Boolean? = false
    private var _wordSet = setOf<String>()

    private val _matchState = MutableStateFlow(MatchModel())
    val matchState = _matchState.asStateFlow()

    // 6x5 grid
    private val _gridState = MutableStateFlow(mutableListOf<MutableList<CellModel>>())
    val gridState = _gridState.asStateFlow()

    private val _currentCell = MutableStateFlow(CellModel())

    // 3-rows keyboard
    private val _keyboardState = MutableStateFlow(listOf<MutableList<KeyModel>>())
    val keyboardState = _keyboardState.asStateFlow()

    private val _konfettiState = MutableStateFlow<KonfettiState>(KonfettiState.Idle)
    val konfettiState = _konfettiState.asStateFlow()


    init {
        // init grid
        _gridState.value = MutableList(6) { row ->
            MutableList(5) { col ->
                CellModel(coords = row to col)
            }
        }

        // init keyboard
        _keyboardState.value = listOf(
            listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p").mapIndexed { colIdx, letter ->
                KeyModel(
                    letter = letter,
                    coords = 0 to colIdx
                )
            }.toMutableList(),
            listOf("a", "s", "d", "f", "g", "h", "j", "k", "l").mapIndexed { colIdx, letter ->
                KeyModel(
                    letter = letter,
                    coords = 1 to colIdx
                )
            }.toMutableList(),
            listOf("z", "x", "c", "v", "b", "n", "m").mapIndexed { colIdx, letter ->
                KeyModel(
                    letter = letter,
                    coords = 2 to colIdx
                )
            }.toMutableList(),
        )
    }

    // FIREBASE & LOCAL updates

    fun fetchMatch(
        matchId: String,
        isMatchCreatedByUser: Boolean,
        friendId: String,
        ctx: Context
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _wordSet = loadWordSet(ctx = ctx).await()
            _isMatchCreatedByUser = isMatchCreatedByUser

            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(_user.uid)
            val friendRef = db.collection("users").document(friendId)
            val matchesRef = db.collection("matches")
            val matchDoc = matchesRef.document(matchId).get().await()
            val matchData: MutableMap<String, Any?>

            // always init new player data for the user, regardless if it is a new match or not
            val userPlayer = PlayerModel(user = userRef)
            val friendPlayer = PlayerModel(user = friendRef)

            // if a match already exists in db it was created by friend (player1), who sent the challenge
            if (!isMatchCreatedByUser && matchDoc.exists()) {
                val docData = matchDoc.data!!
                // start by fetching only the necessary fields
                matchData = mutableMapOf(
                    "player2" to userPlayer,
                    "winner" to docData["winner"].toString(),
                    "word" to docData["word"].toString()
                )
                // otherwise create a new match (now user is player1)
            } else {
                matchData = mutableMapOf(
                    "player1" to userPlayer,
                    "player2" to friendPlayer,
                    "timestamp" to FieldValue.serverTimestamp(), // computed by Firestore
                    "winner" to "",
                    "word" to _wordSet.elementAt(Random.nextInt(_wordSet.size))
                )
                matchesRef.document(matchId).set(matchData)
            }

            // in any case, update the local match state through the populated matchData
            _matchState.update {
                it.copy(
                    id = matchId,
                    user = userPlayer,
                    friend = friendPlayer,
                    winner = matchData["winner"].toString(),
                    word = matchData["word"].toString(),
                )
            }
        }
    }

    private fun updateFirestoreUser() {
        // now the user has played, to make the timer stop
        val userPlayer = _matchState.updateAndGet {
            it.copy(
                user = it.user.copy(
                    hasPlayed = true
                )
            )
        }.user
        _hasUserPlayed = true

        viewModelScope.launch(Dispatchers.IO) {
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            val matchRef = db.collection("matches").document(_matchState.value.id)
            val userPlayerNumber = if (_isMatchCreatedByUser!!) "player1" else "player2"
            val updatedUserData = mapOf(
                "${userPlayerNumber}.hasPlayed" to userPlayer.hasPlayed,
                "${userPlayerNumber}.attempts" to userPlayer.attempts,
                "${userPlayerNumber}.duration" to userPlayer.duration
            )

            matchRef.update(updatedUserData)
        }
    }

    // GRID

    private fun buildGuess(rowIdx: Int): String {
        // join the letters from each Cell in the row
        return _gridState.value[rowIdx].fastJoinToString(separator = "") {
            it.letter
        }
    }

    // MATCH

    private fun loadWordSet(ctx: Context): Deferred<Set<String>> {
        return viewModelScope.async(Dispatchers.IO) {
            ctx.resources.openRawResource(R.raw.words_it).use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line: String?
                val words = mutableSetOf<String>()
                while (
                    reader.readLine()
                        .also { line = it } // assign the line on the fly
                    != null
                ) {
                    words.add(line!!)
                }
                // return the list of words to make it awaitable
                words
            }
        }
    }

    private fun increaseAttempts() {
        val attempts = _matchState.value.user.attempts + 1
        _matchState.update {
            it.copy(
                user = it.user.copy(
                    attempts = attempts
                )
            )
        }
    }

    private fun startTimer() {
        var duration = _matchState.value.user.duration
        viewModelScope.launch {
            while (!_hasUserPlayed) {
                // increment it every second
                _matchState.update {
                    it.copy(
                        user = it.user.copy(
                            duration = duration++
                        )
                    )
                }
                delay(1000L)
            }
        }
    }

    fun validateGuess(
        onInvalidWord: () -> Unit,
        showMatchResultDialog: () -> Unit,
        registerMatchToApi: (Int, Int) -> Unit,
    ) {
        val gridCopy = _gridState.value.map { row -> row.toMutableList() }.toMutableList()
        val keyboardCopy = _keyboardState.value.map { row -> row.toMutableList() }.toMutableList()
        val currRowIdx = _currentCell.value.coords.first
        val word = _matchState.value.word
        val guess = buildGuess(rowIdx = currRowIdx)

        // check if invalid word
        if (!_wordSet.contains(guess)) {
            // reset each cell in the current row
            gridCopy[currRowIdx] = MutableList(5) { colIdx ->
                CellModel(coords = currRowIdx to colIdx)
            }
            _gridState.value = gridCopy
            onInvalidWord()
            return
        }

        // continue iff valid word
        increaseAttempts()

        // check guess chars position
//        var nOfHighlights = 0 // delay(colIdx * (++nOfHighlights))
        val guessCharsInWord = mutableSetOf<Char>()
        val guessCharsInRightPlace = mutableSetOf<Char>()
        guess.forEachIndexed { colIdx, char ->
            val keyPressed = keyboardCopy.flatten().find {
                it.letter == char.toString()
            }!!
            val (keyRowIdx, keyColIdx) = keyPressed.coords
            var isLetterInRightPlace = false
            var isLetterInWord = false
            var isKeyAvailable = true

            // first, check if green
            if (char == word[colIdx]) {
                isLetterInRightPlace = true
                guessCharsInRightPlace.add(char)
            }
            // otherwise, check if yellow (uniquely for the same letters)
            else if (word.contains(char) && !guessCharsInWord.contains(char) && !guessCharsInRightPlace.contains(
                    char
                )
            ) {
                isLetterInWord = true
                guessCharsInWord.add(char)
            }
            // else, the letter is not in the word (only if not green nor yellow), so update keyboard only
            else if (!guessCharsInWord.contains(char) && !guessCharsInRightPlace.contains(char)) {
                isKeyAvailable = false
            }

            // update grid
            gridCopy[currRowIdx][colIdx] = gridCopy[currRowIdx][colIdx].copy(
                isLetterInRightPlace = isLetterInRightPlace,
                isLetterInWord = isLetterInWord,
            )
            // update keyboard
            keyboardCopy[keyRowIdx][keyColIdx] = keyboardCopy[keyRowIdx][keyColIdx].copy(
                isLetterInRightPlace = isLetterInRightPlace,
                isLetterInWord = isLetterInWord,
                isAvailable = isKeyAvailable,
            )
        }

        _gridState.value = gridCopy
        _keyboardState.value = keyboardCopy
        // move the focus to the next row - first cell (if it isn't already the last row)
        _currentCell.value = _gridState.value[(currRowIdx + 1).coerceAtMost(5)][0]

        // check if the word is guessed
        if (guess == word) {
            // show konfetti
            setKonfettiState(KonfettiState.Started)
            // finally
            showMatchResultDialog()
            registerMatchToApi(
                _matchState.value.user.attempts,
                _matchState.value.user.duration
            )
            updateFirestoreUser()
            // and so also when the last attempt fails (== 6)
        } else if (_matchState.value.user.attempts >= 6) {
            // reset attempts
            _matchState.update {
                it.copy(
                    user = it.user.copy(
                        attempts = 0
                    )
                )
            }
            // finally
            showMatchResultDialog()
            registerMatchToApi(
                _matchState.value.user.attempts,
                _matchState.value.user.duration
            )
            updateFirestoreUser()
        }
    }

// KEYBOARD

    fun onKeyPress(key: KeyModel) {
        // if not already going, start the timer upon touching the keyboard
        if (_matchState.value.user.duration == 0) {
            startTimer()
        }

        // update the current cell (the first empty cell)
        val currRowIdx = _currentCell.value.coords.first
        val newLetter = key.letter
        // shallow copy
        val gridCopy = _gridState.value.map { row -> row.toMutableList() }.toMutableList()

        gridCopy[currRowIdx].forEachIndexed { colIdx, cell ->
            // upon reaching an empty cell
            if (cell.letter.isEmpty()) {
                // set it and exit
                gridCopy[currRowIdx][colIdx] = cell.copy(letter = newLetter)
                _gridState.value = gridCopy
                _currentCell.value = gridCopy[currRowIdx][colIdx]
                return
            }
        }
    }

    fun onBackspacePress() {
        // update the current cell (the last non-empty cell)
        val currRowIdx = _currentCell.value.coords.first
        // shallow copy
        val gridCopy = _gridState.value.map { row -> row.toMutableList() }.toMutableList()

        for (colIdx in gridCopy[currRowIdx].indices.reversed()) {
            val cell = gridCopy[currRowIdx][colIdx]
            // upon reaching an full cell
            if (cell.letter.isNotEmpty()) {
                // empty it and exit
                gridCopy[currRowIdx][colIdx] = cell.copy(letter = "")
                _gridState.value = gridCopy
//                val newColIdx = if (colIdx > 0) colIdx - 1 else 0
                _currentCell.value = gridCopy[currRowIdx][colIdx.coerceAtLeast(0)]
                return
            }
        }
    }

// KONFETTI

    fun setKonfettiState(konfettiState: KonfettiState) {
        _konfettiState.value = konfettiState
    }
}