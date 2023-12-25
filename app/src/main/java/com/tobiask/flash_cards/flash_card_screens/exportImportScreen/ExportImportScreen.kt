package com.tobiask.flash_cards.flash_card_screens.exportImportScreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.DecksDAO
import com.tobiask.flash_cards.R


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
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) viewModel.import(uri)
    }

    Scaffold {
        it
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { viewModel.export() }) {
                Icon(imageVector = Icons.Default.IosShare, contentDescription = stringResource(id = R.string.export))
                Text(text = stringResource(id = R.string.export), fontSize = 25.sp)
            }
            Button(onClick = { launcher.launch("*/*") }) {
                Icon(imageVector = Icons.Default.SaveAlt, contentDescription = stringResource(id = R.string.import_string))
                Text(text = stringResource(id = R.string.import_string),  fontSize = 25.sp)
            }
        }
    }
}