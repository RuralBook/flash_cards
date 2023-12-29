package com.tobiask.flash_cards.models

import androidx.annotation.DrawableRes
import com.tobiask.flash_cards.R

sealed class OnBoardingPage(
    @DrawableRes
    val image: Int,
    val title: Int,
    val description: Int
)
{
    object First: OnBoardingPage(
        image = R.drawable.ic_launcher_round,
        title = R.string.intro_title_01,
        description = R.string.intro_content_01
    )
    object Second: OnBoardingPage(
        image = R.drawable.folder_icon_round,
        title = R.string.intro_title_02,
        description = R.string.intro_content_02
    )
    object Third: OnBoardingPage(
        image = R.drawable.lightbulb_icon_round,
        title = R.string.intro_title_03,
        description = R.string.intro_content_03
    )
}