package com.tobiask.flash_cards.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Deck::class, Card::class, Config::class, Folder::class, Stats::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [

    ]
)
abstract class FlashCardsDatabase: RoomDatabase() {

    abstract val decksDao: DecksDAO
    abstract val cardsDao: CardsDao
    //abstract val configDao: ConfigDao
    abstract val folderDao: FolderDao
    abstract val statsDao: StatsDao
}