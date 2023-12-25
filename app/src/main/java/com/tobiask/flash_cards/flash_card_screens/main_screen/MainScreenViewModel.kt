package com.tobiask.flash_cards.flash_card_screens.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobiask.flash_cards.database.Card
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.Deck
import com.tobiask.flash_cards.database.DecksDAO
import com.tobiask.flash_cards.database.Folder
import com.tobiask.flash_cards.database.FolderDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainScreenViewModel(val dao: DecksDAO, val folderDao: FolderDao, val cardsDao: CardsDao): ViewModel() {
    private val _showPopUp = MutableStateFlow(false)
    val showPopUp = _showPopUp.asStateFlow()

    val cards = cardsDao.getAllCardsDueTo()

    fun popUp(){
        _showPopUp.value = !_showPopUp.value
    }


    //DATABASE FUNKTIONEN:
    //----------------------------------------------------------------------------------------------


    fun addDeck(deck: Deck){
        viewModelScope.launch {
            dao.addDeck(deck)
        }
    }

    fun addFolder(folder: Folder){
        viewModelScope.launch {
            folderDao.insertFolder(folder)
        }
    }
    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            folderDao.deleteFolder(folder)
        }
    }
    fun delDeckByFolder(folder: Folder) {
        viewModelScope.launch {
            dao.delAllDecksWithParent(folder.id)
        }
    }

    fun getCardsToLearn(cards: List<String>): Int{
        var toLearn = 0
        for(card in cards){
            val dueTo = LocalDate.parse(card)
            val today = LocalDate.now()
            if (dueTo.isBefore(today) || dueTo.isEqual(today)){
                toLearn++
            }
        }
        return toLearn
    }

    fun delDeck(deck: Deck) {
        viewModelScope.launch {
            cardsDao.deleteCardsByDeckId(deck.id)
            dao.deleteDeck(deck)
        }
    }

    fun deleteCards(id: Int){
        viewModelScope.launch {
            cardsDao.deleteCardsByDeckId(id)
        }
    }
}