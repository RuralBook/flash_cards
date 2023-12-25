package com.tobiask.flash_cards.flash_card_screens.statistics_screen

import androidx.lifecycle.ViewModel
import com.tobiask.flash_cards.database.StatsDao

class StatisticsScreenViewModel(val statsDao: StatsDao): ViewModel() {
    val stats = statsDao.getStats()
}