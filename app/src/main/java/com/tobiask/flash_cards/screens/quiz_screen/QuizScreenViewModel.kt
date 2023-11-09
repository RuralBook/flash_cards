package com.tobiask.flash_cards.screens.quiz_screen

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobiask.flash_cards.QuizCards
import com.tobiask.flash_cards.database.Card
import com.tobiask.flash_cards.database.CardsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.system.exitProcess

class QuizScreenViewModel(val dao: CardsDao): ViewModel() {


    /*private val _cardFace= MutableStateFlow(CardFace.Front)
    val cardFace = _cardFace.asStateFlow()*/

    fun converter(cards: List<Card>): List<QuizCards>{
        val quizCards = mutableListOf<QuizCards>()
        for (card in cards){
            val buffer = QuizCards(id = card.id, frontSide = card.front, frontSideImg = Uri.parse(card.frontImg) ,backSide = card.back, backSideImg = Uri.parse(card.backImg) , oldDifficulty = card.difficulty, difficulty = card.difficulty, difficultyTimes = card.difficultyTimes)
            quizCards.add(buffer)
        }
        return quizCards
    }

    fun getQuizCards(cards: List<Card>): List<Card>{
        val ids = mutableListOf<Card>()
        val today = LocalDate.now()
        for(card in cards){
            val date = LocalDate.parse(card.dueTo)
            //Log.e("datum", "$date +  $today")
            if (date.isBefore(today) || date.isEqual(today)){
                ids.add(card)
            }
        }
        return ids
    }

    fun getURI(id: Int): Flow<String?>{
        return dao.getUri(id)
    }

    fun again(card: QuizCards, dao: CardsDao, deckId:Int): QuizCards{
        return if(card.difficulty == 0 && card.difficultyTimes == 1){
            card.difficulty = 1
            card.difficultyTimes = 0
            viewModelScope.launch {
                dao.updateCard(Card(id = card.id, deckId = deckId ,front = card.frontSide, back = card.backSide, difficulty = card.difficulty, difficultyTimes =  card.difficultyTimes, dueTo = LocalDate.now().plusDays(1).toString()))

            }
            //Log.e("system", Runtime.getRuntime().maxMemory().toString())
            card
        } else {
            card.difficulty = 0
            card.difficultyTimes = 1
            viewModelScope.launch {
                dao.updateCard(Card(id = card.id, deckId = deckId ,front = card.frontSide, back = card.backSide, difficulty = card.difficulty, difficultyTimes =  card.difficultyTimes, dueTo = LocalDate.now().plusDays(0).toString()))
            }
            card
        }
    }

    fun dificult(card: QuizCards, dao: CardsDao, deckId:Int): QuizCards{
        if (card.difficulty == 1) card.difficultyTimes +=1 else card.difficultyTimes = 0
        card.difficulty = 1
        viewModelScope.launch {
            dao.updateCard(Card(id = card.id, deckId = deckId, front = card.frontSide, back = card.backSide, difficulty = card.difficulty, difficultyTimes =  card.difficultyTimes, dueTo = LocalDate.now().plusDays(1).toString()))
        }
        return card
    }

    fun well(card: QuizCards, dao: CardsDao, deckId:Int): QuizCards{
        if (card.difficulty == 2) card.difficultyTimes +=1 else card.difficultyTimes = 0
        card.difficulty = 2
        viewModelScope.launch {
            dao.updateCard(Card(id = card.id, deckId = deckId ,front = card.frontSide, back = card.backSide, difficulty = card.difficulty, difficultyTimes =  card.difficultyTimes, dueTo = LocalDate.now().plusDays(3).toString()))
        }
        return card
    }

    fun easy(card: QuizCards, dao: CardsDao, deckId:Int): QuizCards{
        if (card.difficulty == 3){
            if (card.difficultyTimes == 2){
                card.difficulty = 4
                card.difficultyTimes = 1
                viewModelScope.launch {
                    dao.updateCard(Card(id = card.id, deckId = deckId ,front = card.frontSide, back = card.backSide, difficulty = card.difficulty, difficultyTimes =  card.difficultyTimes, dueTo = LocalDate.now().plusDays(14).toString()))
                }
                return card
            } else {
                card.difficultyTimes++
                viewModelScope.launch {
                    val updatedCard = Card(
                        id = card.id,
                        deckId = deckId,
                        front = card.frontSide,
                        back = card.backSide,
                        difficulty = card.difficulty,
                        difficultyTimes = card.difficultyTimes,  // Ensure that it reflects the updated value
                        dueTo = LocalDate.now().plusDays(7).toString()
                    )
                    // Update the card in the database
                    dao.updateCard(updatedCard)
                }
                return card
            }
        }
        if (card.difficulty == 4){
            if (card.difficultyTimes == 3){
                card.difficulty = 5
                card.difficultyTimes = 1
                viewModelScope.launch {
                    dao.updateCard(
                        Card(
                            id = card.id,
                            deckId = deckId,
                            front = card.frontSide,
                            back = card.backSide,
                            difficulty = card.difficulty,
                            difficultyTimes = card.difficultyTimes,
                            dueTo = LocalDate.now().plusDays(30).toString()
                        )
                    )
                }
                return card
            } else {
                card.difficultyTimes++
                viewModelScope.launch {
                    dao.updateCard(
                        Card(
                            id = card.id,
                            deckId = deckId,
                            front = card.frontSide,
                            back = card.backSide,
                            difficulty = card.difficulty,
                            difficultyTimes = card.difficultyTimes,
                            dueTo = LocalDate.now().plusDays(14).toString()
                        )
                    )
                }
                return card
            }
        }
        if (card.difficulty == 5){
            card.difficultyTimes++
            viewModelScope.launch {
                dao.updateCard(
                    Card(
                        id = card.id,
                        deckId = deckId,
                        front = card.frontSide,
                        back = card.backSide,
                        difficulty = card.difficulty,
                        difficultyTimes = card.difficultyTimes,
                        dueTo = LocalDate.now().plusDays(30).toString()
                    )
                )
            }
            return card
        }
        card.difficulty = 3
        card.difficultyTimes =1
        viewModelScope.launch {
            dao.updateCard(
                Card(
                    id = card.id,
                    deckId = deckId,
                    front = card.frontSide,
                    back = card.backSide,
                    difficulty = card.difficulty,
                    difficultyTimes = card.difficultyTimes,
                    dueTo = LocalDate.now().plusDays(7).toString()
                )
            )
        }
        return card
    }


}