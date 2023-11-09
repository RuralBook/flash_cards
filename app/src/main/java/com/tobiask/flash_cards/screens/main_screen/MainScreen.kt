package com.tobiask.flash_cards.screens.main_screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.tobiask.flash_cards.database.ConfigDao
import com.tobiask.flash_cards.database.Deck
import com.tobiask.flash_cards.database.DecksDAO
import com.tobiask.flash_cards.navigation.Screen
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class
)
@Composable
fun MainScreen(
    navController: NavController,
    dao: DecksDAO,
    configDao: ConfigDao,
    cardsDao: CardsDao
) {

    // View Model Impl.
    val context = LocalContext.current
    val viewModel = viewModel<MainScreenViewModel>(
        factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainScreenViewModel(dao, configDao, cardsDao) as T
            }
        }
    )

    rememberScrollState()
    val popupStateAdd by viewModel.showPopUp.collectAsState()
    //val setUp by viewModel.configDao.getSetUpData().collectAsState(initial = emptyList())
    val decks = viewModel.dao.getAllDecks().collectAsState(initial = emptyList())
    if (popupStateAdd) {
        AddDeck(viewModel = viewModel, context)
    }
    /*if (setUp.isEmpty()){
    SetUp(viewModel = viewModel, context = context)
}*/


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
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.welcome),
                                fontSize = 39.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                softWrap = true
                            )
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
                        viewModel.deleteCards(row.id)
                        viewModel.delDeck(row)
                    }
                ) {
                    DeckCard(deck = row, viewModel, navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DeckCard(deck: Deck, viewModel: MainScreenViewModel, navController: NavController) {
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
                verticalArrangement = Arrangement.Top,
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
            }
        }
    }
}

// PopUp
//--------------------------------------------------------------------------------------------------

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetUp(viewModel: MainScreenViewModel, context: Context){
val textState = remember { mutableStateOf(TextFieldValue()) }
var secondSetting by remember {
    mutableStateOf(false)
}
val lang = "German"
val name = textState.value.text

@Composable
fun Lang(){
    Text(text = "Coming Soon!")
}

@Composable
fun Name(){
    TextField(value = textState.value, onValueChange = {textState.value = it}, maxLines = 1)
}

AlertDialog(
    onDismissRequest = { viewModel.popUpSetUp() },
    confirmButton =
    {
        if (!secondSetting)
            //Language Screen
            secondSetting = true
        else
            // Name Screen
            if (name.isNotBlank()) {
                viewModel.setUpDB(Config(name = name, language = lang))
                viewModel.popUpSetUp()
            } else{
                Toast.makeText(context, "pls enter a valid Name!", Toast.LENGTH_SHORT).show()
            }
    },
    title = { Text(text = "Set Up")},
    text = {
        if (!secondSetting)
            Lang()
        else
            Name()
    },
)
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeck(viewModel: MainScreenViewModel, context: Context) {
    val textState = remember { mutableStateOf(TextFieldValue()) }
    AlertDialog(
        onDismissRequest = {
            viewModel.popUp()
        },
        confirmButton = {
            Button(
                onClick = {
                    if (textState.value.text.isNotEmpty()) {
                        viewModel.popUp()
                        viewModel.addDeck(Deck(name = textState.value.text))
                    } else {
                        Toast.makeText(context, "Bitte gib einen Namen ein", Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                Text(text = "ok")
            }
        },
        title = {
            Text(text = "Neues Deck")
        },
        text = {
            TextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                label = { Text(text = "Name des Decks") }
            )
        }
    )
}