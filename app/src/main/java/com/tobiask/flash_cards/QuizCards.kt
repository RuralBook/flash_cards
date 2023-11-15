package com.tobiask.flash_cards

import android.net.Uri
import androidx.compose.runtime.MutableState

data class QuizCards(
    val id: Int,
    val frontSide: String,
    val frontSideImg: Uri?,
    val backSide: String,
    val backSideImg: Uri?,

    val oldDifficulty: Int,
    var difficulty: Int,
    var difficultyTimes:Int,
)
