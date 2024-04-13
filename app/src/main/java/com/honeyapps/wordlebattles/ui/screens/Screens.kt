package com.honeyapps.wordlebattles.ui.screens

import androidx.annotation.StringRes
import com.honeyapps.wordlebattles.R

enum class Screens(@StringRes val title: Int) {
    /* Enumeration constants are used as navigation routes */

    SignIn(title = R.string.sign_in),
    Menu(title = R.string.menu),
    Home(title = R.string.home),
    Profile(title = R.string.profile),
    Settings(title = R.string.settings),
    Friends(title = R.string.friends),
    Shop(title = R.string.shop),
    FeedbackAndSupport(title = R.string.feedback_and_support),
    HowToPlay(title = R.string.how_to_play),
    About(title = R.string.about),
}