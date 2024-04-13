package com.honeyapps.wordlebattles.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.DialogsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TextFieldDialog(
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    modifier: Modifier = Modifier,
    dialogsViewModel: DialogsViewModel = koinViewModel()
) {
    val textFieldDialogVisibility by dialogsViewModel.textFieldDialogVisibility.collectAsStateWithLifecycle()
    if (!textFieldDialogVisibility) {
        onDismiss()
        return
    }

    var filledText by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false // true
        )
    ) {
        // container
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.secondary
        ) {
            // text field
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(vertical = dimens.paddingXL)
            ) {
                OutlinedTextField(
                    value = filledText,
                    onValueChange = {
                        filledText = it
                    },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    placeholder = {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    // show X when there is text
                    trailingIcon = {
                        if (filledText.isNotEmpty()) {
                            IconButton(
                                // clear the text and hide the keyboard
                                onClick = {
                                    filledText = ""
                                    keyboardController?.hide()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        imeAction = if (filledText.isEmpty()) ImeAction.Done else ImeAction.Go,
                        keyboardType = KeyboardType.Text,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onDismiss()
                        },
                        onGo = {
                            if (filledText.isNotEmpty()) {
                                onConfirm(filledText) // invoke confirmation
                                onDismiss()
                            }
                        }
                    ),
                    colors = TextFieldDefaults.colors(
//                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                        focusedLabelColor = Color.LightGray,
//                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.secondary,
//                        focusedPlaceholderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(dimens.spacerM))

                // buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Button(
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Button(
                        onClick = {
                            onConfirm(filledText) // invoke confirmation
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.confirm),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                }
            }
        }
    }
}