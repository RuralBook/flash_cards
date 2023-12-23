package com.tobiask.flash_cards.flash_card_screens.exportImportScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tobiask.flash_cards.database.CardsDao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportImportScreen(id: Int, dao: CardsDao){
    val viewModel =
        viewModel<ExportImportScreenViewModel>(factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ExportImportScreenViewModel(id, dao) as T
            }
        })

    Scaffold {it
        Column {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Export")
            }
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Import")
            }
        }
    }
}