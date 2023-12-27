package com.tobiask.flash_cards.flash_card_screens.deck_screen_menu

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobiask.flash_cards.QuizCards
import com.tobiask.flash_cards.database.Card
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.Deck
import com.tobiask.flash_cards.database.DecksDAO
import com.tobiask.flash_cards.database.Stats
import com.tobiask.flash_cards.database.StatsDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class DeckScreenMenuViewModel(val dao: DecksDAO, val daoCards: CardsDao, val statsDao: StatsDao, val deckId: Int) :
    ViewModel() {

    var _cards = MutableStateFlow( mutableListOf<QuizCards>())
    val cards = _cards.asStateFlow()

    val DeckCards = daoCards.getCards(deckId)

    var _isFetched = MutableStateFlow(false)
    val isFetched = _isFetched.asStateFlow()

    fun getCards(){
        var cards = mutableListOf<QuizCards>()
        viewModelScope.launch {
            runBlocking {
                _cards.value = converter(daoCards.getCardsList(deckId)).toMutableList()
            }
        }
    }

    fun addCard(card: QuizCards){
        _cards.value = _cards.value.toMutableList().apply { add(card) }
    }

    private val _showPopUpAdd = MutableStateFlow(false)
    val showPopUpAdd = _showPopUpAdd.asStateFlow()

    private val _editCard = MutableStateFlow(
        Card(
            front = "",
            back = "",
            dueTo = "",
            difficulty = 0,
            deckId = 0
        )
    )
    val editCard = _editCard.asStateFlow()

    private val _showPopUpEdit = MutableStateFlow(false)
    val showPopUpEdit = _showPopUpEdit.asStateFlow()

    private val _showPopUpEditCard = MutableStateFlow(false)
    val showPopUpEditCard = _showPopUpEditCard.asStateFlow()

    private var _known = MutableStateFlow(0)
    val known = _known.asStateFlow()
    private var _unknown = MutableStateFlow(0)
    val unknown = _unknown.asStateFlow()
    private var _ok_known = MutableStateFlow(0)
    val ok_known = _ok_known.asStateFlow()

    fun addKnown() {
        _known.value++
    }

    fun addUnKnown() {
        _unknown.value++
    }

    fun addOkKnown() {
        _ok_known.value++
    }

    fun editCardValue(card: Card) {
        _editCard.value = card
    }

    fun popUpAdd() {
        _showPopUpAdd.value = !_showPopUpAdd.value
    }

    fun popUpEdit() {
        _showPopUpEdit.value = !_showPopUpEdit.value
    }

    fun popUpEditCard() {
        _showPopUpEditCard.value = !_showPopUpEditCard.value
    }

    fun addDeck(deck: Deck) {
        viewModelScope.launch {
            dao.addDeck(deck)
        }
    }

    fun addCard(card: Card) {
        viewModelScope.launch {
            daoCards.addCard(card)
        }
    }

    fun delOneCard(card: Card) {
        viewModelScope.launch {
            daoCards.deleteOneCard(card)
        }
    }

    fun updateCard(card: Card) {
        viewModelScope.launch {
            daoCards.updateCard(card)
        }
    }
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

    fun converter(cards: List<Card>): List<QuizCards> {
        if (!_isFetched.value) {
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
            }
            _isFetched.value = true
            return quizCards.shuffled()
        }
        var quizCards = mutableListOf<QuizCards>()
        cards.forEach {
            val buffer = QuizCards(
                id = it.id,
                frontSide = it.front,
                frontSideImg = null,
                backSide = it.back,
                backSideImg = null,
                oldDifficulty = it.difficulty,
                difficulty = it.difficulty,
                difficultyTimes = it.difficultyTimes
            )
            quizCards.add(buffer)
        }
        return quizCards.shuffled()

    }

    fun again(card: QuizCards, dao: CardsDao, deckId: Int): QuizCards {
        return if (card.difficulty == 0 && card.difficultyTimes == 1) {
            card.difficulty = 1
            card.difficultyTimes = 0
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
            card
        } else {
            card.difficulty = 0
            card.difficultyTimes = 1
            /*viewModelScope.launch {
                dao.updateCard(
                    Card(
                        id = card.id,
                        deckId = deckId,
                        front = card.frontSide,
                        back = card.backSide,
                        difficulty = card.difficulty,
                        difficultyTimes = card.difficultyTimes,
                        dueTo = LocalDate.now().plusDays(0).toString()
                    )
                )
            }*/
            card
        }
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