package com.tobiask.flash_cards.database

import android.net.Uri
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DecksDAO {
    @Upsert(Deck::class)
    suspend fun addDeck(decks: Deck)

    @Delete(Deck::class)
    suspend fun deleteDeck(deck: Deck)

    @Query("SELECT * FROM deck WHERE id LIKE :id1")
    fun getDeck(id1: Int): Flow<Deck>

    @Query("SELECT * FROM deck WHERE parentFolder LIKE 0 ORDER BY important desc")
    fun getAllDecks(): Flow<List<Deck>>

    @Query("SELECT * FROM deck WHERE parentFolder LIKE :parent")
    fun getAllDecksWithParent(parent: Int): Flow<List<Deck>>

    @Query("DELETE FROM deck WHERE parentFolder LIKE :parent")
    suspend fun delAllDecksWithParent(parent: Int)

    @Update(Deck::class)
    suspend fun addImportance(decks: Deck)
}

@Dao
interface CardsDao {
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
    @Query("SELECT * FROM folder WHERE id LIKE :id")
    fun getFolder(id: Int): Flow<Folder>
    @Insert(Folder::class)
    suspend fun insertFolder(folder: Folder)

    @Delete(Folder::class)
    suspend fun deleteFolder(folder: Folder)

    @Update(Folder::class)
    suspend fun updateFolder(folder: Folder)

    @Query("SELECT * FROM folder")
    fun getAllFolder(): Flow<List<Folder>>

    @Query("SELECT * FROM folder WHERE parentFolder like :id")
    fun getAllFolderById(id: Int): Flow<List<Folder>>
}