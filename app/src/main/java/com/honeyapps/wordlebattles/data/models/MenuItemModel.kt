package com.honeyapps.wordlebattles.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.honeyapps.wordlebattles.R
import com.honeyapps.wordlebattles.ui.screens.Screens

@Immutable
sealed class MenuItemModel(
    open val resId: Int? = null,
    open val label: String,
    open val route: String? = null,
    open val icon: ImageVector,
    open val navIcon: ImageVector? = Icons.AutoMirrored.Filled.NavigateNext,
    open val sectionResId: Int? = null,
    open val badgeCount: Int? = null
) {
    data class Settings(
        override val label: String,
        override val route: String = Screens.Settings.name,
        override val icon: ImageVector = Icons.Filled.Settings,
        override val sectionResId: Int? = R.string.section_game,
    ) : MenuItemModel(
        label = label,
        route = route,
        icon = icon,
    )

    data class Friends(
        override val label: String,
        override val route: String = Screens.Friends.name,
        override val icon: ImageVector = Icons.Filled.Groups,
        override val sectionResId: Int? = R.string.section_game,
        override var badgeCount: Int = 0 // var!
    ) : MenuItemModel(
        label = label,
        route = route,
        icon = icon,
    )

    data class Shop(
        override val label: String,
        override val route: String = Screens.Shop.name,
        override val icon: ImageVector = Icons.Filled.ShoppingCart,
        override val sectionResId: Int? = R.string.section_game,
    ) : MenuItemModel(
        label = label,
        route = route,
        icon = icon,
    )

    data class InviteYourFriends(
        override val resId: Int?,
        override val label: String,
        override val icon: ImageVector = Icons.Filled.Share,
        override val navIcon: ImageVector? = null,
        override val sectionResId: Int? = R.string.section_share,
    ) : MenuItemModel(
        resId = resId,
        label = label,
        icon = icon,
    )

    data class FeedbackAndSupport(
        override val label: String,
        override val route: String = Screens.FeedbackAndSupport.name,
        override val icon: ImageVector = Icons.Filled.Mail,
        override val sectionResId: Int? = R.string.section_share,
    ) : MenuItemModel(
        label = label,
        route = route,
        icon = icon,
    )

    data class HowToPlay(
        override val label: String,
        override val route: String = Screens.HowToPlay.name,
        override val icon: ImageVector = Icons.Filled.QuestionMark,
        override val sectionResId: Int? = R.string.section_info,
    ) : MenuItemModel(
        label = label,
        route = route,
        icon = icon,
    )

    data class About(
        override val label: String,
        override val route: String = Screens.About.name,
        override val icon: ImageVector = Icons.Filled.Info,
        override val sectionResId: Int? = R.string.section_info,
    ) : MenuItemModel(
        label = label,
        route = route,
        icon = icon,
    )

    data class SignOut(
        override val label: String,
        override val route: String = Screens.SignIn.name,
        override val icon: ImageVector = Icons.AutoMirrored.Filled.Logout,
        override val navIcon: ImageVector? = null,
    ) : MenuItemModel(
        label = label,
        icon = icon,
    )

    data class DeleteAccount(
        override val label: String,
        override val route: String = Screens.SignIn.name,
        override val icon: ImageVector = Icons.Filled.DeleteSweep,
        override val navIcon: ImageVector? = null,
    ) : MenuItemModel(
        label = label,
        icon = icon,
    )
}