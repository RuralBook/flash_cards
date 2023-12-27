package com.tobiask.flash_cards.database

import android.net.Uri
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DecksDAO {

    @Query("SELECT MAX(id) FROM deck")
    suspend fun getMaxDeckId(): Int?

    @Transaction
    suspend fun getNextDeckId(): Int {
        val maxId = getMaxDeckId() ?: 0
        return maxId + 1
    }

    @Upsert(Deck::class)
    suspend fun addDeck(decks: Deck)

    @Delete(Deck::class)
    suspend fun deleteDeck(deck: Deck)

    @Query("SELECT * FROM deck WHERE id LIKE :id1")
    fun getDeck(id1: Int): Flow<Deck>

    @Query("SELECT name FROM deck WHERE id Like :id")
    suspend fun getDeckName(id: Int): List<String>
    @Query("SELECT * FROM deck")
    fun getAllDecks(): Flow<List<Deck>>

    @Query("SELECT * FROM deck")
    suspend fun getAllDecksStatic(): List<Deck>

    @Query("SELECT id FROM deck WHERE parentFolder LIKE 0 ORDER BY important desc")
    fun getAllDecksId(): Flow<List<Int>>

    @Query("SELECT * FROM deck WHERE parentFolder LIKE :parent")
    fun getAllDecksWithParent(parent: Int): Flow<List<Deck>>
    @Query("SELECT * FROM deck WHERE parentFolder LIKE :parent")
    suspend fun getAllDecksWithParentStatic(parent: Int): List<Deck>

    @Query("DELETE FROM deck WHERE parentFolder LIKE :parent")
    suspend fun delAllDecksWithParent(parent: Int)

    @Update(Deck::class)
    suspend fun addImportance(decks: Deck)
}

@Dao
interface CardsDao {

    @Query("SELECT MAX(id) FROM card")
    suspend fun getMaxCardId(): Int?

    @Transaction
    suspend fun getNextCardId(): Int {
        val maxId = getMaxCardId() ?: 0
        return maxId + 1
    }
    @Insert(Card::class)
    suspend fun addCard(card: Card)

    @Update(Card::class)
    suspend fun updateCard(card: Card)

    @Delete(Card::class)
    suspend fun deleteOneCard(card: Card)

    @Query("DELETE FROM card WHERE deckId LIKE :id")
    suspend fun deleteCardsByDeckId(id: Int)

    @Query("SELECT * FROM card WHERE deckId LIKE :id")
    fun getCards(id: Int): Flow<List<Card>>

    @Query("SELECT * FROM card WHERE deckId LIKE :id")
    suspend fun getCardsList(id: Int): List<Card>

    @Query("SELECT * FROM card")
    fun getAllCards(): Flow<List<Card>>

    @Query("SELECT * FROM card")
    suspend fun getAllCardsStatic(): List<Card>

    @Query("SELECT dueTo FROM card")
    fun getAllCardsDueTo(): Flow<List<String>>

    @Query("SELECT dueTo FROM card WHERE deckId LIKE :id")
    fun getCardsDueTo(id: Int): Flow<List<String>>


    @Query("SELECT backImg AND frontImg FROM card WHERE id LIKE :id1")
    fun getUri(id1: Int): Flow<String>
}

@Dao
interface ConfigDao {
    @Insert(Config::class)
    suspend fun insertConfig(config: Config)

    @Query("SELECT * FROM config WHERE id LIKE 1")
    fun getSetUpData(): Flow<List<Config>>
}

@Dao
interface FolderDao {


    @Query("SELECT MAX(id) FROM folder")
    suspend fun getMaxFolderId(): Int?

    @Transaction
    suspend fun getNextFolderId(): Int {
        val maxId = getMaxFolderId() ?: 0
        return maxId + 1
    }

    @Insert(Folder::class)
    suspend fun insertFolder(folder: Folder)
    @Query("SELECT * FROM folder WHERE id LIKE :id")
    fun getFolder(id: Int): Flow<Folder>
    @Delete(Folder::class)
    suspend fun deleteFolder(folder: Folder)

    @Upsert(Folder::class)
    suspend fun updateFolder(folder: Folder)

    @Query("SELECT * FROM folder")
    fun getAllFolder(): Flow<List<Folder>>

    @Query("SELECT * FROM folder")
    suspend fun getAllFoldersStatic(): List<Folder>

    @Query("SELECT * FROM folder WHERE parentFolder like :id")
    fun getAllFolderById(id: Int): Flow<List<Folder>>
}

@Dao
interface StatsDao {
    @Update(Stats::class)
    suspend fun updateStats(stats: Stats)

    @Insert(Stats::class)
    suspend fun addStats(stats: Stats)

    @Query("SELECT * FROM Stats WHERE id LIKE 1")
    fun getStats(): Flow<List<Stats>>

    @Query("SELECT * FROM Stats WHERE id LIKE 1")
    suspend fun getStatsStatic(): List<Stats>
}