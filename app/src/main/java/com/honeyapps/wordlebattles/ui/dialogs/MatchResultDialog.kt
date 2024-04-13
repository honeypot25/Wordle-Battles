package com.honeyapps.wordlebattles.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.DialogsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MatchResultDialog(
    attempts: Int,
    duration: Int,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier,
    dialogsViewModel: DialogsViewModel = koinViewModel()
) {
    val matchResultDialogVisibility by dialogsViewModel.matchResultDialogVisibility.collectAsStateWithLifecycle()
    if (!matchResultDialogVisibility) {
        onBackHome()
        return
    }

    val title = if (attempts > 0) {
        // word was guessed
        stringResource(id = R.string.match_well_done)
        // word not guessed
    } else { stringResource(id = R.string.match_failed) }

    AlertDialog(
        title = {
            Text(
                text = title,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(dimens.spacerS))
        },
        text = {
            Column {
                Text(
                    text = stringResource(id = R.string.match_duration, duration),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(dimens.spacerXS))
                Text(
                    text = stringResource(id = R.string.match_attempts, attempts),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(dimens.spacerS))
            }
        },
        dismissButton = {
            Button(
                onClick = { onBackHome() }
            ) {
                Text(
                    text = stringResource(id = R.string.match_back_home),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                )
            }
        },
        onDismissRequest = { onBackHome() },
        confirmButton = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        containerColor = MaterialTheme.colorScheme.secondary,
        titleContentColor = MaterialTheme.colorScheme.onSecondary,
        textContentColor = MaterialTheme.colorScheme.onSecondary,
        shape = MaterialTheme.shapes.small
    )
}