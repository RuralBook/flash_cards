package com.tobiask.flash_cards.flash_card_screens.deck_screen_menu

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobiask.flash_cards.QuizCards
import com.tobiask.flash_cards.database.Card
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.Deck
import com.tobiask.flash_cards.database.DecksDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class DeckScreenMenuViewModel(val dao: DecksDAO, val daoCards: CardsDao, deckId: Int) : ViewModel() {

    val cards = daoCards.getCards(deckId)

    private val _showPopUpAdd = MutableStateFlow(false)
    val showPopUpAdd = _showPopUpAdd.asStateFlow()

    private val _showPopUpEdit = MutableStateFlow(false)
    val showPopUpEdit = _showPopUpEdit.asStateFlow()

    private val _showPopUpEditCard = MutableStateFlow(false)
    val showPopUpEditCard = _showPopUpEditCard.asStateFlow()

    fun editCardValue(card: Card): Card{
        return card
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

    fun converter(cards: List<Card>): MutableList<QuizCards>{
        val quizCards = mutableListOf<QuizCards>()
        val today = LocalDate.now()
        for (card in cards.shuffled()){
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
        return quizCards.toMutableList()
    }

    fun converterTrainer(cards: List<Card>): MutableList<QuizCards>{
        val quizCards = mutableListOf<QuizCards>()
        for (card in cards.shuffled()){
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
        return quizCards.toMutableList()
    }

    fun again(card: QuizCards, dao: CardsDao, deckId:Int): QuizCards{
        return if(card.difficulty == 0 && card.difficultyTimes == 1){
            card.difficulty = 1
            card.difficultyTimes = 0
            viewModelScope.launch {
                dao.updateCard(Card(id = card.id, deckId = deckId ,front = card.frontSide, back = card.backSide, difficulty = card.difficulty, difficultyTimes =  card.difficultyTimes, dueTo = LocalDate.now().plusDays(1).toString()))

            }
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

    fun difficult(card: QuizCards, dao: CardsDao, deckId:Int): QuizCards{
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