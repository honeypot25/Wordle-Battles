package com.honeyapps.wordlebattles.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.honeyapps.wordlebattles.ui.screens.Screens

@Immutable
sealed class BottomNavBarItemModel(
    open val label: String,
    open val route: String,
    open val selectedIcon: ImageVector,
    open val unselectedIcon: ImageVector,
    open val badgeCount: Int? = null,
) {
    data class Menu(
        override val label: String,
        override val route: String = Screens.Menu.name,
        override val selectedIcon: ImageVector = Icons.Filled.Menu,
        override val unselectedIcon: ImageVector = Icons.Outlined.Menu,
        override var badgeCount: Int = 0, // var!
    ) : BottomNavBarItemModel(
        label = label,
        route = route,
        selectedIcon = selectedIcon,
        unselectedIcon = unselectedIcon
    )

    data class Home(
        override val label: String,
        override val route: String = Screens.Home.name,
        override val selectedIcon: ImageVector = Icons.Filled.Home,
        override val unselectedIcon: ImageVector = Icons.Outlined.Home,
    ) : BottomNavBarItemModel(
        label = label,
        route = route,
        selectedIcon = selectedIcon,
        unselectedIcon = unselectedIcon
    )

    data class Profile(
        override val label: String,
        override val route: String = Screens.Profile.name,
        override val selectedIcon: ImageVector = Icons.Filled.Person,
        override val unselectedIcon: ImageVector = Icons.Outlined.Person,
    ) : BottomNavBarItemModel(
        label = label,
        route = route,
        selectedIcon = selectedIcon,
        unselectedIcon = unselectedIcon
    )
}