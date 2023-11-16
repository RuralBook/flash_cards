package com.tobiask.flash_cards.flash_card_screens.main_screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
fun MainScreen(
    navController: NavController,
    dao: DecksDAO,
    folderDao: FolderDao,
    cardsDao: CardsDao
) {

    // View Model Impl.
    val context = LocalContext.current
    val viewModel = viewModel<MainScreenViewModel>(
        factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainScreenViewModel(dao, folderDao, cardsDao) as T
            }
        }
    )

    rememberScrollState()
    val popupStateAdd by viewModel.showPopUp.collectAsState()
    val mainDecks = viewModel.dao.getAllDecksWithParent(0).collectAsState(initial = emptyList())
    val folder = viewModel.folderDao.getAllFolder().collectAsState(initial = emptyList())
    if (popupStateAdd) {
        AddDeck(viewModel = viewModel, context)
    }

    var decksShown by remember {
        mutableStateOf(true)
    }


    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),//.height(80.dp),
                title = {
                    Column() {
                        Row(
                            modifier = Modifier.fillMaxWidth(),//.fillMaxHeight(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(50.dp)) {
                                Text(
                                    text = "",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    softWrap = true
                                )
                            }


                            Text(
                                text = stringResource(id = R.string.welcome),
                                fontSize = 39.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                softWrap = true
                            )

                            Box(Modifier.size(50.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "settings"
                                )
                            }

                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),//.fillMaxHeight(),
                            horizontalArrangement = Arrangement.Center
                        ) {

                        }
                    }
                },

                )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.popUp() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .padding(
                        end = 20.dp,
                        start = 20.dp,
                        top = it.calculateTopPadding() + 15.dp,
                        bottom = it.calculateBottomPadding()
                    )
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { decksShown = true }, enabled = !decksShown) {
                            Text(text = stringResource(id = R.string.decks), fontSize = 20.sp)
                        }
                        TextButton(onClick = { decksShown = false }, enabled = decksShown) {
                            Text(text = stringResource(id = R.string.folder), fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                }
                if (decksShown) {
                    itemsIndexed(mainDecks.value) { _, row ->
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
                                viewModel.deleteCards(row.id)
                                viewModel.delDeck(row)
                            }
                        ) {
                            DeckCard(deck = row, navController, viewModel)
                        }
                    }
                } else {
                    itemsIndexed(folder.value) { _, row ->
                        val decks1 = viewModel.dao.getAllDecksWithParent(row.id)
                            .collectAsState(initial = emptyList())
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
                                for (deck in decks1.value) {
                                    viewModel.deleteCards(deck.id)
                                }
                                viewModel.delDeckByFolder(row)
                                viewModel.deleteFolder(row)

                            }
                        ) {
                            FolderCard(folder = row, navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DeckCard(deck: Deck, navController: NavController, viewModel: MainScreenViewModel) {
    val cardsToLearn = viewModel.getToLearnCards(deck.id)
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
                    val routeWithArgs = "${Screen.DeckScreen.route}?id=${deck.id}"
                    navController.navigate(routeWithArgs)
                }
            )
        ) {
            Column(
                Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                        Text(
                            modifier = Modifier.padding(top = 20.dp),
                            text = deck.name,
                            fontSize = 25.sp,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
                Row {
                    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
                        Text(
                            modifier = Modifier.padding(bottom = 20.dp),
                            text = cardsToLearn.toString(),
                            fontSize = 20.sp,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderCard(folder: Folder, navController: NavController) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(175.dp)
            .padding(bottom = 10.dp)
            .background(MaterialTheme.colorScheme.background),
        border = BorderStroke(2.5.dp, MaterialTheme.colorScheme.onBackground)
    ) {
        Box(Modifier
            .fillMaxSize()
            .combinedClickable(
                onClick = {
                    val routeWithArgs = "${Screen.FolderScreen.route}?id=${folder.id}"
                    navController.navigate(routeWithArgs)
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
                            modifier = Modifier.padding(top = 20.dp),
                            text = folder.name,
                            fontSize = 25.sp,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }
        }
    }
}

// PopUp
//--------------------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeck(viewModel: MainScreenViewModel, context: Context) {
    val name = remember { mutableStateOf(TextFieldValue()) }
    var isDeck by remember { mutableStateOf(true) }
    AlertDialog(
        onDismissRequest = {
            viewModel.popUp()
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.value.text.isNotEmpty()) {
                        if (isDeck) {
                            viewModel.popUp()
                            viewModel.addDeck(Deck(name = name.value.text))
                        } else {
                            viewModel.popUp()
                            viewModel.addFolder(Folder(name = name.value.text, parentFolder = 0))
                        }
                    } else {
                        Toast.makeText(context, R.string.please_enter_a_name, Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                Text(text = stringResource(id = R.string.add))
            }
        },
        title = {
            TextButton(onClick = { isDeck = !isDeck }) {
                Text(
                    text = if (isDeck) stringResource(id = R.string.new_deck) else stringResource(
                        id = R.string.new_folder
                    ), fontSize = 20.sp
                )
            }
        },
        text = {
            TextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = {
                    Text(
                        text = if (isDeck)
                            stringResource(id = R.string.name_of_the_deck) else stringResource(id = R.string.name_of_the_folder)
                    )
                }
            )
        }
    )
}