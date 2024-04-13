package com.honeyapps.wordlebattles.ui.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.DialogsViewModel
import com.honeyapps.wordlebattles.ui.viewmodels.UserViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoSourceDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    onFail: () -> Unit,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = koinViewModel(),
    dialogsViewModel: DialogsViewModel = koinViewModel()
) {
    val photoSourceDialogVisibility by dialogsViewModel.photoSourceDialogVisibility.collectAsStateWithLifecycle()
    if (!photoSourceDialogVisibility) {
        onDismiss()
        return
    }

    val ctx = LocalContext.current
    val cameraPermState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )
//    val galleryPermState = rememberPermissionState(
//        android.Manifest.permission.READ_MEDIA_IMAGES
//    )

    var gallerySelected by remember { mutableStateOf(false) }
    var cameraSelected by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf(Uri.EMPTY) }

    var isCameraPhotoTaken by remember { mutableStateOf(false) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        // set to true when tempFileUri receives bytes when saving photo
        isCameraPhotoTaken = it
    }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {},
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
        icon = {
            Icon(
                imageVector = Icons.Filled.Photo,
                contentDescription = null
            )
        },
        title = {
            Text(text = stringResource(id = R.string.photo_source))
        },
        text = {
            // photo source entries
            Column(
                modifier = Modifier
                    .padding(dimens.paddingS),
                verticalArrangement = Arrangement.Center
            ) {
                // Gallery entry
                PhotoSourceEntry(
                    text = stringResource(id = R.string.photo_source_gallery),
                    imageVector = Icons.Filled.PhotoLibrary,
                    onClick = {
                        gallerySelected = true
                        cameraSelected = false
                    }
                )

                Spacer(modifier = Modifier.height(dimens.spacerM))

                // Camera entry
                PhotoSourceEntry(
                    text = stringResource(id = R.string.photo_source_camera),
                    imageVector = Icons.Filled.CameraAlt,
                    onClick = {
                        cameraSelected = true
                        gallerySelected = false
                    }
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

    if (gallerySelected) {
        val galleryLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
                uri?.let {
                    userViewModel.changePhoto(
                        photoUri = uri,
                        onSuccess = onSuccess,
                        onFail = onFail,
                        ctx = ctx
                    )
                    onDismiss()
                }
            }

        // simply launch PickVisualMedia
        SideEffect {
            galleryLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    }

    LaunchedEffect(key1 = cameraSelected, key2 = cameraPermState.status) {
        if (cameraSelected) {
            if (!cameraPermState.status.isGranted) {
                cameraPermState.launchPermissionRequest()
            } else {
                // create a (cache) temp destination file
                val tempFile = File.createTempFile(
                    "JPG_" + SimpleDateFormat(
                        "yyyyMMdd_HHmmss",
                        Locale.getDefault()
                    ).format(Date()),
                    ".jpg",
                    ctx.externalCacheDir
                )
                // get its URI to save the photo
                photoUri = FileProvider.getUriForFile(
                    ctx,
                    "${ctx.packageName}.fileprovider",
                    tempFile
                )
                // launch Camera
                cameraLauncher.launch(photoUri)
            }
        }
    }

    if (isCameraPhotoTaken) {
        userViewModel.changePhoto(
            photoUri = photoUri,
            isFromCamera = true,
            onSuccess = onSuccess,
            onFail = onFail,
            ctx = ctx
        )
        onDismiss()
    }
}

@Composable
fun PhotoSourceEntry(
    imageVector: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Gallery source
    Row(
        modifier = Modifier
            .fillMaxWidth()
//            .padding(4.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(dimens.spacerMS))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}