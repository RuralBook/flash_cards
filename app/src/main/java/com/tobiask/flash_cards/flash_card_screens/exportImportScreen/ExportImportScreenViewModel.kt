package com.tobiask.flash_cards.flash_card_screens.exportImportScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobiask.flash_cards.database.Card
import com.tobiask.flash_cards.database.CardsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class ExportImportScreenViewModel(val id: Int ,val dao: CardsDao): ViewModel() {

    private fun export(){
        var cards: List<Card>
        viewModelScope.launch {
            cards = dao.getCardsList(id)
        }
    }
}