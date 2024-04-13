package com.honeyapps.wordlebattles.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.graphics.vector.ImageVector
import com.honeyapps.wordlebattles.R

sealed class ProfileItemModel(
    open val label: String,
    open val resId: Int,
    open val icon: ImageVector,
    open var displayedValue: String? = null,
    open val sectionResId: Int,
) {
    data class Username(
        override val label: String,
        override val resId: Int = R.string.username,
        override val icon: ImageVector = Icons.Filled.FormatColorText,
        override var displayedValue: String? = null,
        override val sectionResId: Int = R.string.section_user,
    ) : ProfileItemModel(
        label = label,
        resId = resId,
        icon = icon,
        sectionResId = sectionResId
    )

    data class Photo(
        override val label: String,
        override val resId: Int = R.string.photo,
        override val icon: ImageVector = Icons.Filled.Image,
        override val sectionResId: Int = R.string.section_user,
    ) : ProfileItemModel(
        label = label,
        resId = resId,
        icon = icon,
        sectionResId = sectionResId
    )

    data class Flag(
        override val label: String,
        override val resId: Int = R.string.flag,
        override val icon: ImageVector = Icons.Filled.Flag,
        override var displayedValue: String? = null,
        override val sectionResId: Int = R.string.section_user,
    ) : ProfileItemModel(
        label = label,
        resId = resId,
        icon = icon,
        sectionResId = sectionResId
    )
}