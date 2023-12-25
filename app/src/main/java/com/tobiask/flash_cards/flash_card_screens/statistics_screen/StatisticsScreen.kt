package com.tobiask.flash_cards.flash_card_screens.statistics_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tobiask.flash_cards.database.StatsDao
import com.tobiask.flash_cards.flash_card_screens.main_screen.MainScreenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(statsDao: StatsDao){
    val viewModel = viewModel<StatisticsScreenViewModel>(
        factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StatisticsScreenViewModel(statsDao) as T
            }
        }
    )

    val stats = viewModel.stats.collectAsState(initial = emptyList())

    if (stats.value.isNotEmpty()) {
        Scaffold {
            it
            Column {
                Text(text = "Learned Counter: ${stats.value[0].learnedCounter}")
                Text(text = "Streak: ${stats.value[0].streak}")
            }
        }
    }
}