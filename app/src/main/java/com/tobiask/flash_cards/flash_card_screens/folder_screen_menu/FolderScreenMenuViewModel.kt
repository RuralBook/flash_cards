package com.tobiask.flash_cards.flash_card_screens.folder_screen_menu

import androidx.compose.runtime.collectAsState
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
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.time.LocalDate

class FolderScreenMenuViewModel(val dao: DecksDAO, val daoFolder: FolderDao,val daoCards: CardsDao ,folderId: Int) : ViewModel() {

    val decks = dao.getAllDecksWithParent(folderId)

    private val _showPopUpAdd = MutableStateFlow(false)
    val showPopUpAdd = _showPopUpAdd.asStateFlow()

    private val _showPopUpEditDeck = MutableStateFlow(false)
    val showPopUpEditDeck = _showPopUpEditDeck.asStateFlow()

    private val _showPopUpEditFolder = MutableStateFlow(false)
    val showPopUpEditFolder = _showPopUpEditFolder.asStateFlow()


    fun popUpAdd() {
        _showPopUpAdd.value = !_showPopUpAdd.value
    }

    fun popUpEditDeck() {
        _showPopUpEditDeck.value = !_showPopUpEditDeck.value
    }

    fun popUpEditFolder() {
        _showPopUpEditFolder.value = !_showPopUpEditFolder.value
    }

    fun updateFolder(folder: Folder) {
        viewModelScope.launch {
            daoFolder.updateFolder(folder)
        }
    }


    fun addDeck(deck: Deck) {
        viewModelScope.launch {
            dao.addDeck(deck)
        }
    }


    fun delOneDeck(deck: Deck) {
        viewModelScope.launch {
            dao.deleteDeck(deck)
        }
    }
    fun updateDecks(deck: Deck) {
        viewModelScope.launch {
            dao.addDeck(deck)
        }
    }
}