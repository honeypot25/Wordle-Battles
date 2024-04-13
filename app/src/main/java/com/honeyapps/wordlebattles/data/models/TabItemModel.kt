package com.honeyapps.wordlebattles.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.outlined.CallReceived
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.outlined.CallReceived
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
sealed class TabItemModel(
    open val label: String = "",
    open val selectedIcon: ImageVector,
    open val unselectedIcon: ImageVector,
    open val badgeCount: Int? = null
) {
    data class MyFriends(
        override val label: String,
        override val selectedIcon: ImageVector = Icons.Filled.Groups,
        override val unselectedIcon: ImageVector = Icons.Outlined.Groups,
    ): TabItemModel(
        label = label,
        selectedIcon = selectedIcon,
        unselectedIcon = unselectedIcon
    )

    data class Requests(
        override val label: String,
        override val selectedIcon: ImageVector = Icons.AutoMirrored.Filled.CallReceived,
        override val unselectedIcon: ImageVector = Icons.AutoMirrored.Outlined.CallReceived,
        override var badgeCount: Int = 0 // var!
    ): TabItemModel(
        label = label,
        selectedIcon = selectedIcon,
        unselectedIcon = unselectedIcon
    )

    data class Search(
        override val label: String,
        override val selectedIcon: ImageVector = Icons.Filled.PersonSearch,
        override val unselectedIcon: ImageVector = Icons.Outlined.PersonSearch,
    ): TabItemModel(
        label = label,
        selectedIcon = selectedIcon,
        unselectedIcon = unselectedIcon
    )
}