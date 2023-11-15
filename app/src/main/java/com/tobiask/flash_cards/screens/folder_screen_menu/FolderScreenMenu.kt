package com.tobiask.flash_cards.screens.folder_screen_menu

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tobiask.flash_cards.R
import com.tobiask.flash_cards.database.Deck
import com.tobiask.flash_cards.database.DecksDAO
import com.tobiask.flash_cards.database.Folder
import com.tobiask.flash_cards.database.FolderDao
import com.tobiask.flash_cards.navigation.Screen
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FolderScreenMenu(dao: DecksDAO, daoFolder: FolderDao, id: Int, navController: NavController) {

    val context = LocalContext.current


    val viewModel = viewModel<FolderScreenMenuViewModel>(
        factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FolderScreenMenuViewModel(dao, daoFolder, id) as T
            }
        }
    )
    val folder = viewModel.daoFolder.getFolder(id).collectAsState(initial = Folder(name = "", parentFolder = 0))
    val popUpEdit by viewModel.showPopUpEditFolder.collectAsState()

    val decks = viewModel.decks.collectAsState(initial = emptyList()) //viewModel.daoCards.getCards(deck.value.id).collectAsState(initial = emptyList())

    if (popUpEdit) {
        EditFolder(
            viewModel = viewModel,
            context = context,
            folder = folder.value
        )
    }



    val popUpAdd by viewModel.showPopUpAdd.collectAsState()

    if (popUpAdd) {
        AddDeck(viewModel = viewModel, context = context, folder.value)
    }

    Scaffold(
        topBar = {
            Column(
                Modifier
                    .padding(top = 10.dp, end = 10.dp, start = 10.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer),
            ){
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = folder.value.name,
                        fontSize = 27.5.sp,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        floatingActionButton = {
            Column() {
                FloatingActionButton(onClick = { viewModel.popUpEditFolder() }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                }
                Spacer(modifier = Modifier.height(10.dp))
                FloatingActionButton(onClick = { viewModel.popUpAdd() }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            }
        },
        content = {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(
                    end = 20.dp,
                    start = 20.dp,
                    top = it.calculateTopPadding(),
                    bottom = it.calculateBottomPadding()
                )
            ){
                LazyColumn(Modifier.padding(top = 15.dp)
                ){
                    itemsIndexed(decks.value) { _, row ->
                        RevealSwipe(
                            modifier = Modifier.padding(top = 5.dp, bottom = 15.dp),
                            directions = setOf(
                                RevealDirection.EndToStart
                            ),
                            hiddenContentEnd = {
                                Icon(
                                    modifier = Modifier.padding(horizontal = 25.dp),
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondary
                                )
                            },
                            backgroundCardEndColor = MaterialTheme.colorScheme.secondary,
                            onBackgroundEndClick = {

                            }
                        ) {
                            DeckCardFolderScreen(deck = row, viewModel, context)
                        }

                    }
                }
            }
        }
    )

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DeckCardFolderScreen(deck: Deck, viewModel: FolderScreenMenuViewModel, context: Context) {

    val popUpEditDeck by viewModel.showPopUpEditDeck.collectAsState()

    if (popUpEditDeck) {
            EditDeckFolderScreen(
                viewModel = viewModel,
                context = context,
                deck = deck
            )
    }

    var front by remember {
        mutableStateOf(true)
    }

    val haptic = LocalHapticFeedback.current

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(175.dp)
            .padding(bottom = 10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(Modifier
            .fillMaxSize()
            .combinedClickable(
                onClick = {
                    viewModel.popUpEditFolder()
                },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    front = !front
                }
            )
        ) {
            Column(
                Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                        Text(
                            modifier = Modifier.padding(start = 20.dp, top = 20.dp),
                            text = deck.name,
                            fontSize = 25.sp,
                            fontStyle = if (front) FontStyle.Normal else FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

// POP UPs
//--------------------------------------------------------------------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFolder(viewModel: FolderScreenMenuViewModel, context: Context, folder: Folder) {
    val textState = remember { mutableStateOf(TextFieldValue(folder.name)) }
    AlertDialog(
        onDismissRequest = {
            viewModel.popUpEditFolder()
        },
        confirmButton = {
            Button(
                onClick = {
                    if (textState.value.text.isNotEmpty()) {
                        viewModel.popUpEditFolder()
                        viewModel.updateFolder(Folder(name = textState.value.text, parentFolder = folder.parentFolder))
                    } else {
                        Toast.makeText(context, R.string.please_enter_a_name, Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                Text(text = stringResource(id = R.string.update))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.edit_deck))
        },
        text = {
            TextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                label = { Text(text = stringResource(id = R.string.name_of_the_deck)) }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeck(viewModel: FolderScreenMenuViewModel, context: Context, folder: Folder) {
    val textState = remember { mutableStateOf(TextFieldValue()) }

    AlertDialog(
        onDismissRequest = {
            viewModel.popUpAdd()
        },
        confirmButton = {
            Button(
                onClick = {
                    if (textState.value.text.isNotEmpty()) {
                        viewModel.popUpAdd()
                        viewModel.addDeck(
                            Deck(
                                name = textState.value.text,
                                parentFolder = folder.id
                            )
                        )
                    } else {
                        Toast.makeText(context, R.string.please_enter_a_name, Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
            }
        },
        title = {
            Text(text = stringResource(id = R.string.new_card))
        },
        text = {
            Column {
                TextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    label = { Text(text = stringResource(id = R.string.fronside)) },
                    maxLines = 3,
                    //minLines = 3)
                )

            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDeckFolderScreen(viewModel: FolderScreenMenuViewModel, context: Context, deck: Deck) {
    val textState = remember { mutableStateOf(TextFieldValue(deck.name)) }

    AlertDialog(
        onDismissRequest = {
            viewModel.popUpEditDeck()
        },
        confirmButton = {
            Button(
                onClick = {
                    if (textState.value.text.isNotEmpty()) {
                        val folderUp = Folder(
                            name = deck.name,
                            parentFolder = deck.parentFolder
                        )
                        viewModel.popUpEditDeck()
                    } else {
                        Toast.makeText(context, R.string.please_enter_a_valid_text, Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                Text(text = stringResource(id = R.string.update))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.edit_deck))
        },
        text = {
            Column {
                TextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    label = { Text(text = stringResource(id = R.string.name_of_the_deck)) },
                    maxLines = 4,
                    //minLines = 3
                )
            }
        }
    )
}


