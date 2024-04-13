package com.honeyapps.wordlebattles.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.honeyapps.wordlebattles.data.models.PlayerModel
import com.honeyapps.wordlebattles.data.models.RecentMatchModel
import com.honeyapps.wordlebattles.data.models.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RecentMatchViewModel : ViewModel() {

    private val _user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    private val _userRef = FirebaseFirestore.getInstance().collection("users").document(_user.uid)

    private val _recentMatches = MutableStateFlow(listOf<RecentMatchModel>())
    val recentMatches = _recentMatches.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        fetchRecentMatches()
    }

    fun refreshScreen() {
        // reset
        _isRefreshing.value = false
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchRecentMatches()
            delay(500L)
            _isRefreshing.value = false
        }
    }

    private fun fetchRecentMatches() {
        viewModelScope.launch(Dispatchers.IO) {
            // get the list of user's matches documents from Firestore
            val db = FirebaseFirestore.getInstance()
            val matchesRef = db.collection("matches")
            val userRef = db.collection("users").document(_user.uid)

            // get all the matches where the user is present (so I need to look in both the players) and has played

            // query to retrieve matches where the user is player1
            val p1Query = matchesRef
                .whereEqualTo(FieldPath.of("player1", "user"), userRef)
                .whereEqualTo(FieldPath.of("player1", "hasPlayed"), true)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
            // query to retrieve matches where the user is player2
            val p2Query = matchesRef
                .whereEqualTo(FieldPath.of("player2", "user"), userRef)
                .whereEqualTo(FieldPath.of("player2", "hasPlayed"), true)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
            val querySnaps = Tasks.whenAllSuccess<QuerySnapshot>(
                p1Query.get(),
                p2Query.get()
            ).await()

            if (querySnaps.isEmpty()) return@launch

            // populate those matches
            val matchDocs: List<DocumentSnapshot> = querySnaps.flatMap { it.documents }
            // build the final list of recent matches
            val matches: List<RecentMatchModel> = matchDocs.mapNotNull { matchDoc ->
                matchDoc.data?.let { matchData ->
                    val (userPlayer, friendPlayer) = selectPlayers(matchData)

                    val user = userRef.get().await().data!!
                    val friend = friendPlayer.user!!.get().await().data!!

                    // build a recent match item
                    RecentMatchModel(
                        id = matchDoc.id,
                        user = UserModel(
                            uid = _user.uid,
                            username = user["username"].toString(),
                            photoUrl = user["photoUrl"].toString(),
                            flag = user["flag"].toString()
                        ),
                        userPlayer = userPlayer,
                        friend = UserModel(
                            uid = friendPlayer.user.id,
                            username = friend["username"].toString(),
                            photoUrl = friend["photoUrl"].toString(),
                            flag = friend["flag"].toString()
                        ),
                        friendPlayer = friendPlayer,
                        timestamp = matchData["timestamp"]!! as Timestamp,
                        winner = matchData["winner"].toString(),
                        word = matchData["word"].toString(),
                    )
                }
            }

            // update the local _recentMatches
            _recentMatches.value = matches.sortedByDescending { it.timestamp }
        }
    }

    private fun selectPlayers(
        matchData: Map<String, Any?>
    ): Pair<PlayerModel, PlayerModel> {
        // determine which user is which player, returning first user then friend

        val p1Data = matchData["player1"] as Map<String, Any>
        val p2Data = matchData["player2"] as Map<String, Any>
        val p1 = PlayerModel(
            user = p1Data["user"] as DocumentReference,
            hasPlayed = p1Data["hasPlayed"].toString().toBoolean(),
            attempts = p1Data["attempts"].toString().toInt(),
            duration = p1Data["duration"].toString().toInt(),
        )
        val p2 = PlayerModel(
            user = p2Data["user"] as DocumentReference,
            hasPlayed = p2Data["hasPlayed"].toString().toBoolean(),
            attempts = p2Data["attempts"].toString().toInt(),
            duration = p2Data["duration"].toString().toInt(),
        )

        return if (p1.user == _userRef) {
            p1 to p2
        } else {
            p2 to p1
        }
    }
}
