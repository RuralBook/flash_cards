package com.tobiask.flash_cards.models

import androidx.annotation.DrawableRes
import com.tobiask.flash_cards.R

sealed class OnBoardingPage(
    @DrawableRes
    val image: Int,
    val title: String,
    val description: String
) {
    object First: OnBoardingPage(
        image = R.drawable.ic_launcher_round,
        title = "Welcome!",
        description = "This app should help you to learn everything you ever wanted to learn!"
    )
    object Second: OnBoardingPage(
        image = R.drawable.folder_icon_round,
        title = "How Does this App Work?",
        description = """To get a better Structure you can add Decks to the "root" folder or create Folders where you can insert different decks"""
    )
    object Third: OnBoardingPage(
        image = R.drawable.lightbulb_icon_round,
        title = "How to learn",
        description = "You can learn by using the provided spaced algorithm or using the training mode. Just try them out. \n Have fun and best regards, RuralBook"
    )
}