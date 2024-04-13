package com.honeyapps.wordlebattles.ui.dialogs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.DialogsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConfirmationDialog(
    icon: ImageVector? = null,
    title: String,
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    dialogsViewModel: DialogsViewModel = koinViewModel()
) {
    val confirmationDialogVisibility by dialogsViewModel.confirmationDialogVisibility.collectAsStateWithLifecycle()
    if (!confirmationDialogVisibility) {
        onDismiss()
        return
    }

    AlertDialog(
        icon = {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null
                )
            }
        },
        title = {
            Text(text = title)
            Spacer(modifier = Modifier.height(dimens.spacerS))
        },
        text = {
            Text(text = text)
            Spacer(modifier = Modifier.height(dimens.spacerS))
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() }
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                )
            }
        },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
//                    onDismiss() // done as part of onConfirm()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.confirm),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false // true
        ),
        containerColor = MaterialTheme.colorScheme.secondary,
        iconContentColor = MaterialTheme.colorScheme.primary,
        titleContentColor = MaterialTheme.colorScheme.onSecondary,
        textContentColor = MaterialTheme.colorScheme.onSecondary,
        shape = MaterialTheme.shapes.small
    )
}