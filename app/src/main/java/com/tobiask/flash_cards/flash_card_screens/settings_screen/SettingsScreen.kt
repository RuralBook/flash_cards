package com.tobiask.flash_cards.flash_card_screens.settings_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(){
    val viewModel = viewModel<SettingsScreenViewModel>(
        factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsScreenViewModel() as T
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                Text(text = "Settings")
            }})
        }
    ){it
        Column(
            Modifier
                .padding(it)
                .fillMaxSize()){
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Export Database")
            }
        }
    }
}