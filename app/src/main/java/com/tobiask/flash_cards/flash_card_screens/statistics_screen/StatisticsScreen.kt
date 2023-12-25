package com.tobiask.flash_cards.flash_card_screens.statistics_screen

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tobiask.flash_cards.flash_card_screens.main_screen.MainScreenViewModel


@Composable
fun StatisticsScreen(){
    val viewModel = viewModel<StatisticsScreenViewModel>(
        factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StatisticsScreenViewModel() as T
            }
        }
    )


}