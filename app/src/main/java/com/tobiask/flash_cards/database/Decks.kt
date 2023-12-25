package com.tobiask.flash_cards.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Deck(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    var important: Boolean = false,

    val parentFolder: Int = 0
)

@Entity
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val deckId: Int,

    val folderRoute: Int = 0,

    val front: String,
    val frontImg: String = "",

    val back: String,
    val backImg: String = "",

    val dueTo: String,

    val difficulty: Int = 0,
    var difficultyTimes: Int = 0
)

@Entity
data class Folder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val parentFolder: Int = 0
)

@Entity
data class Config(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val language: String
)

@Entity
data class Stats(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val learnedCounter: Int,

    val streak: Int,

    val lastLearned: String,
)

