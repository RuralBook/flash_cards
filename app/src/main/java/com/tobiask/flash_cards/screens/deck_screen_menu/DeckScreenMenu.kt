package com.tobiask.flash_cards.screens.deck_screen_menu

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.Switch
import androidx.compose.material.TextFieldColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PlayCircleOutline
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tobiask.flash_cards.R
import com.tobiask.flash_cards.database.Card
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.Deck
import com.tobiask.flash_cards.database.DecksDAO
import com.tobiask.flash_cards.navigation.Screen
import com.tobiask.flash_cards.screens.main_screen.DeckCard
import com.tobiask.flash_cards.screens.quiz_screen.CardFace
import com.tobiask.flash_cards.screens.quiz_screen.FlipCard
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DeckScreenMenu(dao: DecksDAO, daoCard: CardsDao, id: Int, navController: NavController) {

    val context = LocalContext.current


    val viewModel = viewModel<DeckScreenMenuViewModel>(
        factory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DeckScreenMenuViewModel(dao, daoCard, id) as T
            }
        }
    )
    val deck = viewModel.dao.getDeck(id).collectAsState(initial = Deck(0, ""))
    val popUpEdit by viewModel.showPopUpEdit.collectAsState()

    val cards = viewModel.cards.collectAsState(initial = emptyList()) //viewModel.daoCards.getCards(deck.value.id).collectAsState(initial = emptyList())

    if (popUpEdit) {
        EditDeck(
            viewModel = viewModel,
            context = context,
            deck.value
        )
    }



    val popUpAdd by viewModel.showPopUpAdd.collectAsState()

    if (popUpAdd) {
        AddCard(viewModel = viewModel, context = context, deck = deck.value)
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
                        text = deck.value.name,
                        fontSize = 27.5.sp,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center){
                    Box(
                        Modifier
                            .clickable {
                                val ids = deck.value.id
                                val routeWithArgs = "${Screen.QuizScreen.route}?id=${ids}"
                                navController.navigate(routeWithArgs)
                            }
                    ){
                        Icon(imageVector = Icons.Default.PlayCircleOutline, contentDescription = null, Modifier.size(35.dp, 35.dp))
                    }
                }
            }
        },
        floatingActionButton = {
            Column() {
                FloatingActionButton(onClick = { viewModel.popUpEdit() }) {
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
                    itemsIndexed(cards.value) { _, row ->
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
                                viewModel.delOneCard(row)
                            }
                        ) {
                            CardCardDeckScreen(card = row, viewModel, context)
                        }

                    }
                }
            }
        }
    )

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardCardDeckScreen(card: Card, viewModel: DeckScreenMenuViewModel, context: Context) {

    val popUpEditCard by viewModel.showPopUpEditCard.collectAsState()

    if (popUpEditCard) {
            EditCard(
                viewModel = viewModel,
                context = context,
                card = card
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
                    viewModel.editCardValue(card)
                    viewModel.popUpEditCard()
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
                            text = if (front) card.front else card.back,
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
fun EditDeck(viewModel: DeckScreenMenuViewModel, context: Context, deck: Deck) {
    val textState = remember { mutableStateOf(TextFieldValue(deck.name)) }
    AlertDialog(
        onDismissRequest = {
            viewModel.popUpEdit()
        },
        confirmButton = {
            Button(
                onClick = {
                    if (textState.value.text.isNotEmpty()) {
                        viewModel.popUpEdit()
                        viewModel.addDeck(Deck(name = textState.value.text, id = deck.id, parentFolder = deck.parentFolder, important = deck.important))
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
fun AddCard(viewModel: DeckScreenMenuViewModel, context: Context, deck: Deck) {
    val textState = remember { mutableStateOf(TextFieldValue()) }
    val textState1 = remember { mutableStateOf(TextFieldValue()) }
    var imageFrontUri by remember {
        mutableStateOf<Uri?>(null)
    }
    /*val launcherFront = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageFrontUri = uri
    }

    var imageBackUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val launcherBack = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageBackUri = uri
    }*/

    AlertDialog(
        onDismissRequest = {
            viewModel.popUpAdd()
        },
        confirmButton = {
            Button(
                onClick = {
                    if (textState.value.text.isNotEmpty() && textState1.value.text.isNotEmpty()) {
                        viewModel.popUpAdd()
                        viewModel.addCard(
                            Card(
                                front = textState.value.text,
                                frontImg = if(imageFrontUri != null)imageFrontUri.toString() else "",
                                back = textState1.value.text,
                                backImg = "",
                                deckId = deck.id,
                                folderRoute = 0,
                                dueTo = LocalDate.now().toString())
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
                /*Row {
                    IconButton(onClick = { launcherFront.launch("image/*") }) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = if (imageFrontUri == null) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(onClick = { imageFrontUri = null }) {
                        Text(text = "Clear image")
                    }
                }*/*/
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = textState1.value,
                    onValueChange = { textState1.value = it },
                    label = { Text(text = stringResource(id = R.string.backside)) },
                    maxLines = 3,
                    //minLines = 3,
                )
                /*Row {
                    IconButton(onClick = { launcherBack.launch("image/*") }) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = if (imageBackUri == null) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(onClick = { imageFrontUri = null }) {
                        Text(text = "Clear image")
                    }
                }*/*/

            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCard(viewModel: DeckScreenMenuViewModel, context: Context, card: Card) {
    val textState = remember { mutableStateOf(TextFieldValue(card.front)) }
    val textState1 = remember { mutableStateOf(TextFieldValue(card.back)) }
    var resetDifficulty by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {
            viewModel.popUpEditCard()
        },
        confirmButton = {
            Button(
                onClick = {
                    if (textState.value.text.isNotEmpty() && textState1.value.text.isNotEmpty()) {
                        val updateCard = Card(
                            id =card.id,
                            deckId = card.deckId,
                            front = textState.value.text,
                            back = textState1.value.text,
                            folderRoute = 0,
                            difficulty = if(!resetDifficulty) card.difficulty else 0,
                            difficultyTimes = if(!resetDifficulty) card.difficultyTimes else 0,
                            dueTo = if(!resetDifficulty) card.dueTo else LocalDate.now().toString()
                        )
                        viewModel.updateCard(
                            updateCard
                        )
                        viewModel.popUpEditCard()
                    } else {
                        Toast.makeText(context, R.string.please_enter_a_valid_text, Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                Text(text = stringResource(id = R.string.update))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.edit_card))
        },
        text = {
            Column {
                TextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    label = { Text(text = stringResource(id = R.string.fronside)) },
                    maxLines = 4,
                    //minLines = 3
                )
                /*Row {
                    IconButton(onClick = { launcherFront.launch("image/*") }) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = if (imageFrontUri == null) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(onClick = { imageFrontUri = null }) {
                        Text(text = "Clear image")
                    }
                }*/*/
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = textState1.value,
                    onValueChange = { textState1.value = it },
                    label = { Text(text = stringResource(id = R.string.backside)) },
                    maxLines = 4,
                    //minLines = 3,
                )
                /*Row {
                    IconButton(onClick = { launcherBack.launch("image/*") }) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = if (imageBackUri == null) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(onClick = { imageBackUri = null }) {
                        Text(text = "Clear image")
                    }
                }*/*/
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = stringResource(id = R.string.reset_difficulty))
                Switch(checked = resetDifficulty, onCheckedChange = {resetDifficulty = it})
            }
        }
    )
}


