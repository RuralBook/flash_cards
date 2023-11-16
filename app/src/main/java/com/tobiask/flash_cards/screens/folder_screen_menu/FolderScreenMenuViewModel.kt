package com.tobiask.flash_cards.screens.folder_screen_menu

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobiask.flash_cards.QuizCards
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
import java.util.Date

class FolderScreenMenuViewModel(val dao: DecksDAO, val daoFolder: FolderDao, folderId: Int) : ViewModel() {

    val decks = dao.getAllDecksWithParent(folderId)

    private val _showPopUpAdd = MutableStateFlow(false)
    val showPopUpAdd = _showPopUpAdd.asStateFlow()

    private val _showPopUpEditDeck = MutableStateFlow(false)
    val showPopUpEditDeck = _showPopUpEditDeck.asStateFlow()

    private val _showPopUpEditFolder = MutableStateFlow(false)
    val showPopUpEditFolder = _showPopUpEditFolder.asStateFlow()

    fun editCardValue(card: Card): Card{
        return card
    }

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

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            daoFolder.deleteFolder(folder)
        }
    }

    fun addDeck(deck: Deck) {
        viewModelScope.launch {
            dao.addDeck(deck)
        }
    }

    fun addFolder(folder: Folder) {
        viewModelScope.launch {
            daoFolder.insertFolder(folder)
        }
    }

    fun delOneDeck(folder:Folder) {
        viewModelScope.launch {
            daoFolder.insertFolder(folder)
        }
    }
    fun updateDecks(folder:Folder) {
        viewModelScope.launch {
            daoFolder.updateFolder(folder)
        }
    }
}