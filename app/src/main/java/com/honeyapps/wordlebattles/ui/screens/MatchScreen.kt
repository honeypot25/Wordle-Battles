package com.honeyapps.wordlebattles.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.data.models.CellModel
import com.honeyapps.wordlebattles.data.models.KeyModel
import com.honeyapps.wordlebattles.ui.dialogs.MatchResultDialog
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.DialogsViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.MatchViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.StatsViewModel
import com.honeyapps.wordlebattles.utils.KonfettiState
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.PartySystem
import org.koin.androidx.compose.koinViewModel

@Composable
fun MatchScreen(
    matchId: String,
    isMatchCreatedByUser: Boolean,
    friendId: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    dialogsViewModel: DialogsViewModel = koinViewModel(),
    matchViewModel: MatchViewModel = koinViewModel(),
    statsViewModel: StatsViewModel = koinViewModel(),
) {
    val ctx = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val gridState by matchViewModel.gridState.collectAsStateWithLifecycle()
    val keyboardState by matchViewModel.keyboardState.collectAsStateWithLifecycle()
    val konfettiState by matchViewModel.konfettiState.collectAsStateWithLifecycle()
    val matchResultDialogVisibility by dialogsViewModel.matchResultDialogVisibility.collectAsStateWithLifecycle()
    val matchState by matchViewModel.matchState.collectAsStateWithLifecycle()

    var compositions by rememberSaveable { mutableIntStateOf(0) }

    // get/create the corresponding match (ignoring unwanted recompositions)
    LaunchedEffect(compositions) {
        if (compositions == 0) {
            matchViewModel.fetchMatch(
                matchId = matchId,
                isMatchCreatedByUser = isMatchCreatedByUser,
                friendId = friendId,
                ctx = ctx
            )
            compositions++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = dimens.spacerL,
                bottom = dimens.spacerL
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // DURATION
        DurationIndicator()

        // GRID
        LazyColumn(
            modifier = Modifier
                .padding(dimens.paddingM),
            verticalArrangement = Arrangement.spacedBy(dimens.spacerXS),
            userScrollEnabled = false
        ) {
            // list of rows
            items(gridState) { row ->
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(dimens.spacerXS),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    userScrollEnabled = false
                ) {
                    items(items = row, key = { it.coords }) { cell ->
                        GridCell(
                            cell = cell,
                        )
                    }
                }
            }
        }

        // SUBMIT BUTTON & BACKSPACE KEY
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.paddingM)
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Button(
                    onClick = {
                        matchViewModel.validateGuess(
                            onInvalidWord = {
                                Toast.makeText(
                                    ctx,
                                    R.string.word_invalid,
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            showMatchResultDialog = {
                                dialogsViewModel.setMatchResultDialogVisibility(true)
                            },
                            registerMatchToApi = { attempts, duration ->
                                // handle the final updates for user match stats (and winner choice) through the API
                                statsViewModel.updateStats(
                                    uid = matchState.user.user!!.id,
                                    matchId = matchState.id,
                                    providedMatch = mapOf(
                                        "attempts" to attempts,
                                        "duration" to duration,
                                    ),
                                )
                            }
                        )
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.submit),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }


            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            ) {
                BackspaceKey(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        matchViewModel.onBackspacePress()
                    },
                )
            }
        }

        // KEYBOARD
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = dimens.paddingM),
            verticalArrangement = Arrangement.spacedBy(dimens.spacerS),
            horizontalAlignment = Alignment.CenterHorizontally,
            userScrollEnabled = false
        ) {
            items(keyboardState) { row ->
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(dimens.spacerS),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    userScrollEnabled = false
                ) {
                    items(items = row, key = { it.coords }) { key ->
                        KeyboardKey(
                            key = key,
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                matchViewModel.onKeyPress(key)
                            }
                        )
                        Spacer(modifier = Modifier.width(dimens.spacerXS))
                    }
                }
            }
        }
    }

    // KONFETTI
    if (konfettiState is KonfettiState.Started) {
        KonfettiView(
            modifier = Modifier.fillMaxSize(),
            parties = (konfettiState as KonfettiState.Started).parties,
            updateListener = object : OnParticleSystemUpdateListener {
                override fun onParticleSystemEnded(
                    system: PartySystem,
                    activeSystems: Int
                ) {
                    if (activeSystems == 0) {
                        matchViewModel.setKonfettiState(KonfettiState.Idle)
                    }
                }
            }
        )
    }

    // RESULT DIALOG
    if (matchResultDialogVisibility && konfettiState is KonfettiState.Idle) {
        val attempts = matchState.user.attempts
        val duration = matchState.user.duration

        MatchResultDialog(
            attempts = attempts,
            duration = duration,
            onBackHome = {
                dialogsViewModel.setMatchResultDialogVisibility(false)
                // clear the whole back stack (excluding start destination)
                navController.navigate(route = navController.graph.startDestinationRoute!!) {
                    popUpTo(navController.graph.startDestinationRoute!!) {
                        inclusive = false
                    }
                }
                // clear the Koin module related to the match
//                KoinModules.clearMatchModule(getKoin())
            }
        )
    }
}

@Composable
fun GridCell(
    cell: CellModel,
    modifier: Modifier = Modifier
) {
    val bgColor = if (cell.isLetterInRightPlace) {
        Color.Green.copy(alpha = 0.7f)
    } else if (cell.isLetterInWord) {
        Color.Yellow.copy(alpha = 0.7f)
        // not in word
    } else {
        MaterialTheme.colorScheme.background
    }

    Box(
        modifier = modifier
            .background(bgColor)
            .border(
                BorderStroke(
                    width = dimens.borderXS,
                    color = Color.LightGray.copy(alpha = 0.7f)
                )
            )
            .padding(dimens.paddingS)
            .size(dimens.squareShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = cell.letter.uppercase(),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = dimens.fontXXL,
            )
        )
    }
}

@Composable
fun KeyboardKey(
    key: KeyModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (key.isLetterInRightPlace) {
        Color.Green.copy(alpha = 0.7f)
    } else if (key.isLetterInWord) {
        Color.Yellow.copy(alpha = 0.7f)
        // not available
    } else if (!key.isAvailable) {
        MaterialTheme.colorScheme.background
    } else {
        MaterialTheme.colorScheme.secondary
    }

    Box(
        modifier = Modifier
            .background(bgColor)
            .clickable(enabled = key.isAvailable) { onClick() }
            .padding(dimens.paddingXS)
            .size(width = dimens.keyWidth, height = dimens.keyHeight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = key.letter.uppercase(),
            color = Color.White,
            fontSize = dimens.fontM,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun BackspaceKey(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
//            .background(MaterialTheme.colorScheme.secondary)
            .clickable { onClick() }
            .size(width = dimens.backspaceWidth, height = dimens.backspaceHeight),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Backspace,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Composable
fun DurationIndicator(
    modifier: Modifier = Modifier,
    matchViewModel: MatchViewModel = koinViewModel()
) {
    val matchState by matchViewModel.matchState.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${matchState.user.duration}s",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
    }
}

