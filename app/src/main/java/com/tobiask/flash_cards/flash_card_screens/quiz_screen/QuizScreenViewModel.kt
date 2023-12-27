package com.tobiask.flash_cards.flash_card_screens.quiz_screen

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobiask.flash_cards.QuizCards
import com.tobiask.flash_cards.database.Card
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.Stats
import com.tobiask.flash_cards.database.StatsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class QuizScreenViewModel(val dao: CardsDao, val statsDao: StatsDao ,  val deckId: Int) : ViewModel() {

//STATS:
//__________________________________________________________________________________________________
    private var _known = MutableStateFlow(0)
    val known = _known.asStateFlow()
    private var _unknown = MutableStateFlow(0)
    val unknown = _unknown.asStateFlow()
    private var _difficult = MutableStateFlow(0)
    val difficult = _difficult.asStateFlow()
    private var _ok_known = MutableStateFlow(0)
    val ok_known = _ok_known.asStateFlow()

    fun addKnown(){ _known.value++}


    fun addUnKnown() { _unknown.value++ }

    fun addDifficult() { _difficult.value++ }

    fun addOkKnown() { _ok_known.value++ }

    fun updateStats(cards: Int){
        var stats = emptyList<Stats>()
        viewModelScope.launch{
            runBlocking {
                stats = statsDao.getStatsStatic()
            }
        }
        val lastLeaned = LocalDate.parse(stats[0].lastLearned)
        val today = LocalDate.now()
        val learnedCounter = stats[0].learnedCounter + 1
        val streak = if (today.minusDays(1) == lastLeaned) stats[0].streak + 1 else if (today == lastLeaned) stats[0].streak else 0
        viewModelScope.launch {
            Log.d("info", "$today, $lastLeaned, $learnedCounter, $streak")
            statsDao.updateStats(
                Stats(
                    id = stats[0].id,
                    learnedCounter = learnedCounter ,
                    streak = streak,
                    lastLearned = LocalDate.now().toString(),
                    learnedCardsCounter = stats[0].learnedCardsCounter + cards,
                    firstUsage = stats[0].firstUsage,
                    achievements = ""
                )
            )
        }
    }

//CARDS:
//__________________________________________________________________________________________________
    var cards = converter()

    var _isFetched = false
    private fun converter(): MutableList<QuizCards> {
        var cards = mutableListOf<Card>()
        viewModelScope.launch {
            runBlocking {
               cards = dao.getCardsList(deckId).toMutableList()
            }
        }
        val quizCards = mutableListOf<QuizCards>()
        val today = LocalDate.now()
        for (card in cards) {
            val date = LocalDate.parse(card.dueTo)
            if (date.isBefore(today) || date.isEqual(today)) {
                val buffer = QuizCards(
                    id = card.id,
                    frontSide = card.front,
                    frontSideImg = null,
                    backSide = card.back,
                    backSideImg = null,
                    oldDifficulty = card.difficulty,
                    difficulty = card.difficulty,
                    difficultyTimes = card.difficultyTimes
                )
                quizCards.add(buffer)
            }
            _isFetched = true
        }
        return quizCards.shuffled().toMutableList()
    }

    fun again(card: QuizCards, dao: CardsDao, deckId: Int): Boolean {

        var toRepeat = false

        if (card.difficulty == 0 && card.difficultyTimes == 1) {
            card.difficulty = 1
            card.difficultyTimes = 0
            toRepeat = false
        } else {
            card.difficulty = 0
            card.difficultyTimes = 1
            toRepeat = true
        }

        viewModelScope.launch {
            runBlocking {
                dao.updateCard(
                    Card(
                        id = card.id,
                        deckId = deckId,
                        front = card.frontSide,
                        back = card.backSide,
                        difficulty = card.difficulty,
                        difficultyTimes = card.difficultyTimes,
                        dueTo = LocalDate.now().plusDays(if (card.difficulty == 0) 0 else 1).toString()
                    )
                )
            }
        }
        return toRepeat
    }


    fun difficult(card: QuizCards, dao: CardsDao, deckId: Int): QuizCards {
        if (card.difficulty == 1) card.difficultyTimes += 1 else card.difficultyTimes = 0
        card.difficulty = 1
        viewModelScope.launch {
            dao.updateCard(
                Card(
                    id = card.id,
                    deckId = deckId,
                    front = card.frontSide,
                    back = card.backSide,
                    difficulty = card.difficulty,
                    difficultyTimes = card.difficultyTimes,
                    dueTo = LocalDate.now().plusDays(1).toString()
                )
            )
        }
        return card
    }

    fun well(card: QuizCards, dao: CardsDao, deckId: Int): QuizCards {
        if (card.difficulty == 2) card.difficultyTimes += 1 else card.difficultyTimes = 0
        card.difficulty = 2
        viewModelScope.launch {
            dao.updateCard(
                Card(
                    id = card.id,
                    deckId = deckId,
                    front = card.frontSide,
                    back = card.backSide,
                    difficulty = card.difficulty,
                    difficultyTimes = card.difficultyTimes,
                    dueTo = LocalDate.now().plusDays(3).toString()
                )
            )
        }
        return card
    }

    fun easy(card: QuizCards, dao: CardsDao, deckId: Int): QuizCards {
        if (card.difficulty == 3) {
            if (card.difficultyTimes == 2) {
                card.difficulty = 4
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
                            dueTo = LocalDate.now().plusDays(14).toString()
                        )
                    )
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
        if (card.difficulty == 4) {
            if (card.difficultyTimes == 3) {
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
        if (card.difficulty == 5) {
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
                    dueTo = LocalDate.now().plusDays(7).toString()
                )
            )
        }
        return card
    }


}