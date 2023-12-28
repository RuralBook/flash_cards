package com.tobiask.flash_cards.flash_card_screens.folder_screen_menu

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tobiask.flash_cards.R
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.Deck
import com.tobiask.flash_cards.database.DecksDAO
import com.tobiask.flash_cards.database.Folder
import com.tobiask.flash_cards.database.FolderDao
import com.tobiask.flash_cards.navigation.Screen
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FolderScreenMenu(
    dao: DecksDAO,
    daoFolder: FolderDao,
    cardsDao: CardsDao,
    id: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel =
        viewModel<FolderScreenMenuViewModel>(factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FolderScreenMenuViewModel(dao, daoFolder, cardsDao, id) as T
            }
        })

    val folder = viewModel.daoFolder.getFolder(id)
        .collectAsState(initial = Folder(name = "", parentFolder = 0))
    val popUpEdit by viewModel.showPopUpEditFolder.collectAsState()

    val decks =
        viewModel.decks.collectAsState(initial = emptyList())

    if (popUpEdit) {
        EditFolder(
            viewModel = viewModel, context = context, folder = folder.value
        )
    }


    val popUpAdd by viewModel.showPopUpAdd.collectAsState()

    if (popUpAdd) {
        AddDeck(viewModel = viewModel, context = context, folder.value)
    }

    Scaffold(topBar = {
        Column(
            Modifier
                .padding(10.dp)
                .clip(shape = RoundedCornerShape(20.dp))
                .border(
                    2.5.dp,
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
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
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }, floatingActionButton = {
        Column() {
            FloatingActionButton(onClick = { viewModel.popUpEditFolder() }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
            Spacer(modifier = Modifier.height(10.dp))
            FloatingActionButton(onClick = { viewModel.popUpAdd() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    }, content = {

        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(
                    end = 20.dp,
                    start = 20.dp,
                    top = it.calculateTopPadding() + 15.dp,
                    bottom = it.calculateBottomPadding()
                ),
            verticalArrangement = Arrangement.Center
        ) {
            itemsIndexed(decks.value) { _, row ->
                RevealSwipe(modifier = Modifier.padding(top = 5.dp, bottom = 15.dp),
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
                        viewModel.delOneDeck(row)
                    }) {
                    DeckCardFolderScreen(deck = row, viewModel, context, navController)
                }
            }
        }
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DeckCardFolderScreen(
    deck: Deck, viewModel: FolderScreenMenuViewModel, context: Context, navController: NavController
) {
    val cardsToLearn =
        viewModel.daoCards.getCardsDueTo(deck.id).collectAsState(initial = emptyList())
    val counter = viewModel.getCardsToLearn(cardsToLearn.value)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(175.dp)
            .padding(bottom = 10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .combinedClickable(onClick = {
                    val routeWithArgs = "${Screen.DeckScreen.route}?id=${deck.id}"
                    navController.navigate(routeWithArgs)
                })
        ) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text(
                            modifier = Modifier.padding(start = 20.dp, top = 20.dp),
                            text = deck.name,
                            fontSize = 25.sp,
                            fontStyle = FontStyle.Normal
                        )
                    }
                }
                Row {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text(
                            modifier = Modifier.padding(bottom = 20.dp),
                            text = counter.toString(),
                            color = Color.Red,
                            fontSize = 20.sp,
                            textDecoration = TextDecoration.Underline
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
    val configuration = LocalConfiguration.current
    val height = (configuration.screenHeightDp.dp / 4) * 1
    Dialog(onDismissRequest = {
        viewModel.popUpEditFolder()
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = stringResource(id = R.string.edit_folder),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                OutlinedTextField(value = textState.value,
                    onValueChange = { textState.value = it },
                    label = { Text(text = stringResource(id = R.string.name_of_the_folder)) },
                    maxLines = 2
                    )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Button(onClick = {
                        if (textState.value.text.isNotEmpty()) {
                            viewModel.popUpEditFolder()
                            viewModel.updateFolder(
                                Folder(
                                    id = folder.id,
                                    name = textState.value.text,
                                    parentFolder = folder.parentFolder
                                )
                            )
                        } else {
                            Toast.makeText(
                                context,
                                R.string.please_enter_a_name,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }) {
                        Text(text = stringResource(id = R.string.update))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeck(viewModel: FolderScreenMenuViewModel, context: Context, folder: Folder) {
    val name = remember { mutableStateOf(TextFieldValue()) }
    val configuration = LocalConfiguration.current
    val height = (configuration.screenHeightDp.dp / 4) * 1
    Dialog(onDismissRequest = {
        viewModel.popUpAdd()
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = stringResource(id = R.string.new_deck),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                OutlinedTextField(value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text(text = stringResource(id = R.string.name_of_the_deck)) },
                    maxLines = 2
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Button(onClick = {
                        if (name.value.text.isNotEmpty()) {
                            viewModel.popUpAdd()
                            viewModel.addDeck(
                                Deck(
                                    name = name.value.text,
                                    parentFolder = folder.id
                                )
                            )
                        } else {
                            Toast.makeText(
                                context,
                                R.string.please_enter_a_name,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }) {
                        Text(text = stringResource(id = R.string.add))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDeckFolderScreen(viewModel: FolderScreenMenuViewModel, context: Context, deck: Deck) {
    val textState = remember { mutableStateOf(TextFieldValue(deck.name)) }

    AlertDialog(onDismissRequest = {
        viewModel.popUpEditDeck()
    }, confirmButton = {
        Button(onClick = {
            if (textState.value.text.isNotEmpty()) {
                val deckUp = Deck(
                    id = deck.id, name = deck.name, parentFolder = deck.parentFolder
                )
                viewModel.updateDecks(deckUp)
                viewModel.popUpEditDeck()
            } else {
                Toast.makeText(
                    context, R.string.please_enter_a_valid_text, Toast.LENGTH_SHORT
                ).show()
            }
        }) {
            Text(text = stringResource(id = R.string.update))
        }
    }, title = {
        Text(text = stringResource(id = R.string.edit_deck))
    }, text = {
        Column {
            TextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                label = { Text(text = stringResource(id = R.string.name_of_the_deck)) },
                maxLines = 4,
                //minLines = 3
            )
        }
    })
}


