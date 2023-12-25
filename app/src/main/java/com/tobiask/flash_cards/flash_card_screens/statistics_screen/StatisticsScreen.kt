package com.tobiask.flash_cards.flash_card_screens.statistics_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tobiask.flash_cards.database.StatsDao
import com.tobiask.flash_cards.flash_card_screens.main_screen.MainScreenViewModel
import com.tobiask.flash_cards.flash_card_screens.statistics_screen.composables.CircularProgressBar


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
        Scaffold(
            topBar = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp), horizontalArrangement = Arrangement.Absolute.Center){
                    Text(text = "Data", style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
                }
            }
        ){it
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = it.calculateTopPadding(),
                        bottom = it.calculateBottomPadding(),
                        start = 50.dp +it.calculateStartPadding(layoutDirection = LayoutDirection.Ltr),
                        end = 50.dp +it.calculateEndPadding(layoutDirection = LayoutDirection.Ltr)
                    )
                    ){
                Spacer(modifier = Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                    Column(Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally){
                        Text(text = "Streak", style = MaterialTheme.typography.headlineMedium)
                        CircularProgressBar(
                            value = stats.value[0].streak,
                            maxValue = 365,
                            fillColor = Color(android.graphics.Color.parseColor("#4DB6AC")),
                            backgroundColor = Color(android.graphics.Color.parseColor("#90A4AE")),
                            strokeWidth = 10.dp
                        )
                    }
                }
                Text(text = "Decks learned: ${stats.value[0].learnedCounter}", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}