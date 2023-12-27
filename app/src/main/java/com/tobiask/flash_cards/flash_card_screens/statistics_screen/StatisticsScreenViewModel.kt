package com.tobiask.flash_cards.flash_card_screens.statistics_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobiask.flash_cards.database.StatsDao
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatisticsScreenViewModel(val statsDao: StatsDao): ViewModel() {
    val stats = statsDao.getStats()

    private var _learnedDecks = MutableStateFlow(0)
    val learnedDecks = _learnedDecks.asStateFlow()

    fun learnedDecksCounter(value: Int){
        viewModelScope.launch{
            while (_learnedDecks.value <= value){
                delay(50)
                _learnedDecks.value++
            }

        }
    }
}