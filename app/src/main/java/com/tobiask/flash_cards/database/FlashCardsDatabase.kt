package com.tobiask.flash_cards.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Deck::class, Card::class, Config::class, Folder::class, Stats::class],
    version = 1,
    exportSchema = false,
    autoMigrations = [
    ]
)
abstract class FlashCardsDatabase: RoomDatabase() {

    abstract val decksDao: DecksDAO
    abstract val cardsDao: CardsDao
    //abstract val configDao: ConfigDao
    abstract val folderDao: FolderDao
    abstract val statsDao: StatsDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Perform the migration SQL statements
                database.execSQL("ALTER TABLE Card ADD COLUMN audioFront TEXT DEFAULT ''")
                database.execSQL("ALTER TABLE Card ADD COLUMN audioBack TEXT DEFAULT ''")
            }
        }
    }
}
