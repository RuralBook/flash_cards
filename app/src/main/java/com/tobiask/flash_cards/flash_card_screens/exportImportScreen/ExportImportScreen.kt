package com.tobiask.flash_cards.flash_card_screens.exportImportScreen

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.DecksDAO


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportImportScreen(id: Int, dao: CardsDao, decksDAO: DecksDAO) {
    val context = LocalContext.current
    val viewModel =
        viewModel<ExportImportScreenViewModel>(factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ExportImportScreenViewModel(id, dao, decksDAO, context) as T
            }
        })
    var json by remember {
        mutableStateOf<Uri?>(null)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) viewModel.import(uri)
    }

    Scaffold {
        it
        Column {
            Button(onClick = { viewModel.export() }) {
                Text(text = "Export")
            }
            Button(
                onClick = {
                    launcher.launch("*/*")
                }) {
                Text(text = "Import")
            }
        }
    }
}