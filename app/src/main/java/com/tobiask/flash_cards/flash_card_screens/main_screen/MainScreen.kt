package com.tobiask.flash_cards.flash_card_screens.main_screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.getString
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
import com.tobiask.flash_cards.database.Stats
import com.tobiask.flash_cards.database.StatsDao
import com.tobiask.flash_cards.navigation.Screen
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    navController: NavController,
    dao: DecksDAO,
    folderDao: FolderDao,
    cardsDao: CardsDao,
    statsDao: StatsDao
) {

    // View Model Impl.
    val context = LocalContext.current
    val viewModel = viewModel<MainScreenViewModel>(
        factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainScreenViewModel(dao, folderDao, cardsDao, statsDao) as T
            }
        }
    )

    val stats = viewModel.stats.collectAsState(
        initial = listOf(
            Stats(
                learnedCounter = 0,
                streak = 0,
                lastLearned = "",
                learnedCardsCounter = 0,
                achievements = "",
                firstUsage = ""
            )
        )
    )

    if (stats.value.isEmpty()) {
        viewModel.insertStatsFirstTime()
        navController.navigate(Screen.OnboardingScreen.route)
    }

    rememberScrollState()
    val popupStateAdd by viewModel.showPopUp.collectAsState()
    val mainDecks = viewModel.dao.getAllDecksWithParent(0).collectAsState(initial = emptyList())
    val folder = viewModel.folderDao.getAllFolderById(0).collectAsState(initial = emptyList())

    if (popupStateAdd) {
        AddDeck(viewModel = viewModel, context)
    }

    var decksShown by remember {
        mutableStateOf(true)
    }


    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),//.fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                navController.navigate(Screen.statsScreen.route)
                            },
                        Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null
                        )
                    }

                    Text(
                        text = stringResource(id = R.string.welcome),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        softWrap = true
                    )
                    Box(
                        Modifier
                            .size(50.dp)
                            .clickable {
                                val routeWithArgs = Screen.SettingsScreen.route
                                navController.navigate(routeWithArgs)
                            },
                        Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
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
    val cardsToLearn =
        viewModel.cardsDao.getCardsDueTo(deck.id).collectAsState(initial = emptyList())
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
                            fontSize = 35.sp,
                            fontFamily = FontFamily(Font(R.font.nunito_bold))
                        )
                    }
                }
                if (counter < 0) {
                    Row {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Text(
                                modifier = Modifier.padding(bottom = 20.dp),
                                text = counter.toString(),
                                fontSize = 20.sp,
                                color = Color.Red,
                                textDecoration = TextDecoration.Underline
                            )
                        }
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
                    Row(
                        Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 20.dp),
                            text = folder.name,
                            fontSize = 35.sp,
                            fontFamily = FontFamily(Font(R.font.nunito_bold)),
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
    var addDeck by remember {
        mutableStateOf(false)
    }
    var addFolder by remember {
        mutableStateOf(false)
    }
    val configuration = LocalConfiguration.current
    val height = (configuration.screenHeightDp.dp / 6) * 2
    Dialog(
        onDismissRequest = {
            viewModel.popUp()
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
                Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start){
                    Text(
                        text =stringResource(id = R.string.new_str)
                        , fontSize = 20.sp,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
                    FilterChip(
                        selected = addDeck,
                        onClick = { addDeck = !addDeck; addFolder = false },
                        label = { Text(text = stringResource(id = R.string.deck)) },
                        leadingIcon = if (addDeck) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        },
                    )
                    FilterChip(
                        selected = addFolder,
                        onClick = { addFolder = !addFolder; addDeck = false },
                        label = { Text(text = stringResource(id = R.string.folder)) },
                        leadingIcon = if (addFolder) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        },
                    )

                }


                OutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = {
                        Text(
                            text = if (addDeck)
                                stringResource(id = R.string.name_of_the_deck) else if (addFolder) stringResource(
                                id = R.string.name_of_the_folder
                            ) else stringResource(id = R.string.please_choose_a_type)
                        )
                    }
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = {
                            if (name.value.text.isNotEmpty()) {
                                if (addDeck) {
                                    viewModel.popUp()
                                    viewModel.addDeck(Deck(name = name.value.text))
                                } else if(addFolder){
                                    viewModel.popUp()
                                    viewModel.addFolder(
                                        Folder(
                                            name = name.value.text,
                                            parentFolder = 0
                                        )
                                    )
                                } else {
                                    Toast.makeText(
                                        context,
                                        getString(context, R.string.please_choose_a_type),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.please_enter_a_name,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                        Text(text = stringResource(id = R.string.add))
                    }
                }
            }
        }
    }
}