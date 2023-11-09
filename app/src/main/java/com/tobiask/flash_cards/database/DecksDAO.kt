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

    @Query("SELECT * FROM deck WHERE parentDecks LIKE '' ORDER BY important desc")
    fun getAllDecks(): Flow<List<Deck>>

    @Query("SELECT * FROM deck WHERE parentDecks LIKE :parent ORDER BY important desc")
    fun getAllDecksWithParent(parent: String): Flow<List<Deck>>

    @Query("DELETE FROM deck WHERE parentDecks LIKE :parent")
    suspend fun delAllDecksWithParent(parent: String)

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

    @Query("SELECT backImg AND frontImg FROM card WHERE id LIKE :id")
    fun getUri(id: Int): Flow<String>
}

@Dao
interface ConfigDao {
    @Insert(Config::class)
    suspend fun insertConfig(config: Config)

    @Query("SELECT * FROM config WHERE id LIKE 1")
    fun getSetUpData(): Flow<List<Config>>
}