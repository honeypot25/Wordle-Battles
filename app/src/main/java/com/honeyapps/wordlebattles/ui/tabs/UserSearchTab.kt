package com.honeyapps.wordlebattles.ui.tabs

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.ui.components.UserItem
import com.honeyapps.wordlebattles.ui.theme.dimens
import com.honeyapps.wordlebattles.ui.viewmodels.FriendsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserSearchTab(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    friendsViewModel: FriendsViewModel = koinViewModel(),
) {
    val ctx = LocalContext.current
    val resultUsersList by friendsViewModel.resultUsersList.collectAsStateWithLifecycle()
    val isSearching by friendsViewModel.isSearching.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UserSearchTextField(
            onSearch = { query ->
                friendsViewModel.onUserSearch(
                    query = query,
                    onSuccess = { nUsersFound ->
                        Toast.makeText(
                            ctx,
                            ctx.getString(R.string.users_search_success, nUsersFound),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onFail = {
                        Toast.makeText(
                            ctx,
                            R.string.users_search_fail,
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                )
            },
        )

        // show CircularProgressIndicator during search
        if (isSearching) {
            Box(
//                modifier = Modifier
//                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.background,
                )
            }
            // or list of users when done
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(top = dimens.paddingM),
            ) {
                items(
                    items = resultUsersList,
                    key = { it.uid } // to make only the interested UserModel recompose, if resultUsersList changes
                ) {
                    UserItem(
                        user = it,
                        navController = navController,
                        isSearchItem = true,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = dimens.paddingS),
                        thickness = dimens.dividerS,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun UserSearchTextField(
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var userQuery by rememberSaveable { mutableStateOf("") }
//    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimens.paddingS),
        value = userQuery,
        onValueChange = {
            userQuery = it
        },
        placeholder = {
            Text(
                text = stringResource(id = R.string.user_search),
                style = MaterialTheme.typography.labelLarge
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        // show X when there is text
        trailingIcon = {
            if (userQuery.isNotEmpty()) {
                IconButton(
                    // clear the query, hide the keyboard and unfocus the TextField
                    onClick = {
                        userQuery = ""
//                        keyboardController?.hide()
                        focusManager.clearFocus()
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
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
            focusedContainerColor = MaterialTheme.colorScheme.secondary,
//            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSecondary,
//            focusedPlaceholderColor = MaterialTheme.colorScheme.onSecondary,
            unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
            focusedTextColor = MaterialTheme.colorScheme.onSecondary,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            imeAction = if (userQuery.isEmpty()) ImeAction.Done else ImeAction.Search,
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            // used only when userQuery is blank
            onDone = {
//                keyboardController?.hide()
                focusManager.clearFocus()
            },
            // send the query, hide the keyboard and unfocus the TextField
            onSearch = {
                if (userQuery.isNotEmpty()) {
                    onSearch(userQuery) // invoke the search
//                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            }
        ),
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}