package com.tobiask.flash_cards.flash_card_screens.settings_screen

import android.net.Uri
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
                Text(text = "Settings")
            }})
        }
    ){it
        Column(
            Modifier
                .padding(it)
                .fillMaxSize()){
            Button(onClick = { /*TODO*/ }) {
                Text(text = "HELP")
            }
            Button(onClick = { viewModel.exportDatabase() }) {
                Text(text = "Backup")
            }
            Button(onClick = { launcher.launch("*/*") }) {
                Text(text = "import")
            }
            Text(text = "NOTE:")
            Text(text = "You can only import a Full-Export to an clean app with no Data!")

        }
    }
}