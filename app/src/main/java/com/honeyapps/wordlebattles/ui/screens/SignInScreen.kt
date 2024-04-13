package com.honeyapps.wordlebattles.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.ui.theme.dimens

// TODO: change the sign in method to Fingerprint after a first Google sign in
@Composable
fun SignInScreen(
    onSignInBtnClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // app icon
        Image(
            painter = painterResource(id = R.drawable.app_image_nobg),
            contentDescription = null,
            modifier = Modifier
                .size(dimens.appImage)
        )

        Spacer(modifier = Modifier.height(dimens.spacerM))

        // Sign in button
        Button(
            onClick = onSignInBtnClick,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = null,
                    modifier = Modifier
                        .size(dimens.iconS)
                )

                Spacer(modifier = Modifier.width(dimens.spacerM))

                Text(
                    text = stringResource(id = R.string.sign_in) + " with Google",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}