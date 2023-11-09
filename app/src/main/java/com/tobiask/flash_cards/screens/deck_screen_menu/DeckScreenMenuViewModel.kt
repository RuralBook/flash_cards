package com.tobiask.flash_cards.screens.deck_screen_menu

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobiask.flash_cards.database.Card
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.Deck
import com.tobiask.flash_cards.database.DecksDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date

class DeckScreenMenuViewModel(val dao: DecksDAO, val daoCards: CardsDao) : ViewModel() {
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

    fun getQuizCards(cards: List<Card>): IntArray{
        var ids = intArrayOf()
        val today = LocalDate.now()
        for(card in cards){
            val date = LocalDate.parse(card.dueTo)
            Log.e("datum", "$date +  $today")
            if (date.isBefore(today) || date.isEqual(today)){
                ids = ids.plus(card.id)
            }
        }
        Log.e("ids", ids.size.toString())
        return ids
    }


}