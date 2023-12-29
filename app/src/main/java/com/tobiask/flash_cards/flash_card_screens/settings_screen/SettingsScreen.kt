package com.tobiask.flash_cards.flash_card_screens.settings_screen

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tobiask.flash_cards.R
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.DecksDAO
import com.tobiask.flash_cards.database.FolderDao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(cardsDao: CardsDao, decksDAO: DecksDAO, folderDao: FolderDao){
    val context = LocalContext.current
    val viewModel = viewModel<SettingsScreenViewModel>(
        factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsScreenViewModel(cardsDao, decksDAO, folderDao, context) as T
            }
        }
    )
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) viewModel.import(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                Text(text = stringResource(id = R.string.settings), style = MaterialTheme.typography.headlineLarge)
            }})
        }
    ){it
        Column(
            Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Button(onClick = { viewModel.exportDatabase() }) {
                Text(text = stringResource(id = R.string.backup))
            }
            Button(onClick = { launcher.launch("*/*") }) {
                Text(text = stringResource(id = R.string.import_str))
            }
            Column {
                Text(text = stringResource(id = R.string.important_note))
                Text(text = stringResource(id = R.string.full_export_note))
            }
            Text(text = stringResource(id = R.string.report_bug_note))
        }
    }
}
