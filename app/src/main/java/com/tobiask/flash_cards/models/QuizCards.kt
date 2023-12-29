package com.tobiask.flash_cards.models

import android.net.Uri

data class QuizCards(
    val id: Int,
    val frontSide: String,
    val frontSideImg: String,
    val backSide: String,
    val backSideImg: String,

    val oldDifficulty: Int,
    var difficulty: Int,
    var difficultyTimes:Int,
)

