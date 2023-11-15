package com.tobiask.flash_cards.screens.main_screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.Config
import com.tobiask.flash_cards.database.ConfigDao
import com.tobiask.flash_cards.database.Deck
import com.tobiask.flash_cards.database.DecksDAO
import com.tobiask.flash_cards.database.Folder
import com.tobiask.flash_cards.database.FolderDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class MainScreenViewModel(val dao: DecksDAO, val folderDao: FolderDao, val cardsDao: CardsDao): ViewModel() {
    private val _showPopUp = MutableStateFlow(false)
    val showPopUp = _showPopUp.asStateFlow()

    private val _showPopUpSetUp = MutableStateFlow(false)
    val showPopUpSetUp = _showPopUpSetUp.asStateFlow()

    fun popUp(){
        _showPopUp.value = !_showPopUp.value
    }

    fun popUpSetUp(){
        _showPopUpSetUp.value = !_showPopUpSetUp.value
    }

    val folder = arrayOf<String>("schule", "privat", "uni", "informatik")


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

    fun addImportance(deck: Deck){
        viewModelScope.launch {
            dao.addImportance(deck)
        }
    }

    fun delDeck(deck: Deck) {
        viewModelScope.launch {
            //dao.delAllDecksWithParent(deck.parentDecks+"/"+deck.name)
            dao.deleteDeck(deck)
        }
    }

    fun deleteCards(id: Int){
        viewModelScope.launch {
            cardsDao.deleteCardsByDeckId(id)
        }
    }
}