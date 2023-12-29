package com.tobiask.flash_cards.flash_card_screens.statistics_screen

import android.graphics.Paint.Align
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tobiask.flash_cards.database.StatsDao
import com.tobiask.flash_cards.ui_elements.AnimatedCounter
import com.tobiask.flash_cards.ui_elements.CircularProgressBar
import com.tobiask.flash_cards.R


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
    val iconSize = 25.dp

    if (stats.value.isNotEmpty()) {
        Scaffold(
            topBar = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp), horizontalArrangement = Arrangement.Start){
                    Text(text = stringResource(id = R.string.statistics), style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
                }
            }
        ){it
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = it.calculateTopPadding(),
                        bottom = it.calculateBottomPadding(),
                        start = 25.dp + it.calculateStartPadding(layoutDirection = LayoutDirection.Ltr),
                        end = 25.dp + it.calculateEndPadding(layoutDirection = LayoutDirection.Ltr)
                    ),
                verticalArrangement = Arrangement.SpaceEvenly
                    ){
                Spacer(modifier = Modifier.height(10.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){

                    Column(Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally){
                        Text(text = "Streak", style = MaterialTheme.typography.headlineMedium)
                        CircularProgressBar(
                            value = stats.value[0].streak,
                            maxValue = 365,
                            fillColor = MaterialTheme.colorScheme.primary, //Color(android.graphics.Color.parseColor("#4DB6AC"))
                            backgroundColor = MaterialTheme.colorScheme.onPrimary, //Color(android.graphics.Color.parseColor("#90A4AE"))
                            strokeWidth = 10.dp
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start){
                    Box(
                        Modifier
                            .padding(16.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,
                        
                        
                    ){
                        Icon(
                            modifier = Modifier.padding(5.dp).size(iconSize),
                            imageVector = Icons.Default.LibraryBooks,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null)
                    }
                    Text(text =  stringResource(id = R.string.learned_decks) + " ", style = MaterialTheme.typography.bodyLarge)
                    Text(text = stats.value[0].learnedCounter.toString())
                    
                }

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start){
                    Box(
                        Modifier
                            .padding(16.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,


                        ){
                        Icon(
                            modifier = Modifier.padding(5.dp).size(iconSize),
                            imageVector = Icons.Default.FolderCopy,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null)
                    }
                    Text(text = stringResource(id = R.string.learned_cards) + " ", style = MaterialTheme.typography.bodyLarge)
                    Text(text = stats.value[0].learnedCardsCounter.toString())

                }
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start){
                    Box(
                        Modifier
                            .padding(16.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,


                        ){
                        Icon(
                            modifier = Modifier.padding(5.dp).size(iconSize),
                            imageVector = Icons.Default.CalendarMonth,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null)
                    }
                    Text(text = stringResource(id = R.string.member_since) + " ${stats.value[0].firstUsage}", style = MaterialTheme.typography.bodyLarge)
                }
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start){
                    Box(
                        Modifier
                            .padding(16.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center,


                        ){
                        Icon(
                            modifier = Modifier.padding(5.dp).size(iconSize),
                            imageVector = Icons.Default.Star,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null,
                        )
                    }
                    Text(text = stringResource(id = R.string.achievements) + " Coming Soon!!", style = MaterialTheme.typography.bodyLarge)
                }
            }

        }
    }

}