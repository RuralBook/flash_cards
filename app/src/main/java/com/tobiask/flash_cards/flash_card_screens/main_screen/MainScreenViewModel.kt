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
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainScreenViewModel(val dao: DecksDAO, val folderDao: FolderDao, val cardsDao: CardsDao): ViewModel() {
    private val _showPopUp = MutableStateFlow(false)
    val showPopUp = _showPopUp.asStateFlow()

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

    fun delDeck(deck: Deck) {
        viewModelScope.launch {
            cardsDao.deleteCardsByDeckId(deck.id)
            dao.deleteDeck(deck)
        }
    }

    fun getToLearnCards(id: Int): Int{
        var cards = emptyList<Card>()
        viewModelScope.launch{
            cards = cardsDao.getCardsToLearn(id)
        }
        var ret = 0
        val today = LocalDate.now()
        for (card in cards){
            val dueTo = LocalDate.parse(card.dueTo)
            if (dueTo.isEqual(today) || dueTo.isBefore(today)){
                ret++
            }
        }
        return  ret
    }

    fun deleteCards(id: Int){
        viewModelScope.launch {
            cardsDao.deleteCardsByDeckId(id)
        }
    }
}