package com.tobiask.flash_cards.flash_card_screens.quiz_screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tobiask.flash_cards.PieChart
import com.tobiask.flash_cards.QuizCards
import com.tobiask.flash_cards.R
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.DecksDAO
import com.tobiask.flash_cards.flash_card_screens.deck_screen_menu.DeckScreenMenuViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.tobiask.flash_cards.PieChartInput


@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(id: Int, dao: CardsDao, dao1: DecksDAO, navController: NavController) {

    val viewModel =
        viewModel<DeckScreenMenuViewModel>(factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DeckScreenMenuViewModel(daoCards = dao, dao = dao1, deckId = id) as T
            }
        })

    val cards = viewModel.converter(viewModel.cards.collectAsState(initial = emptyList()).value)

    val known = viewModel.known.collectAsState().value
    val unknown = viewModel.unknown.collectAsState().value
    val ok_known = viewModel.ok_known.collectAsState().value

    if (cards.isNotEmpty()) {

        Log.d("cardSize", cards.size.toString())


        Scaffold(topBar = {}) {
            Column(
                Modifier
                    .padding(it)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                QuizCard(id, cards, viewModel, dao, navController)
            }
        }
    } else {
        AnalyseScreen(known, ok_known, unknown, navController)
    }
}


@SuppressLint("CoroutineCreationDuringComposition", "UnrememberedMutableState")
@Composable
fun QuizCard(
    id: Int,
    cards: MutableList<QuizCards>,
    viewModel: DeckScreenMenuViewModel,
    cardsDao: CardsDao,
    navController: NavController
) {
    var cardFace by remember {
        mutableStateOf(CardFace.Front)
    }
    var i by remember { mutableIntStateOf(0) }
    Log.d("test", i.toString())
    var card by remember { mutableStateOf(cards[i]) }

    val scrollState = rememberScrollState()

    var frontIsQuestion by remember { mutableStateOf(true) }


    var cardsNotEmpty by remember { mutableStateOf(true) }
    if (cards.size == 0) {
        cardsNotEmpty = false
    }

    val co = rememberCoroutineScope()

    val known = viewModel.known.collectAsState()
    val unknown = viewModel.unknown.collectAsState()
    val ok_known = viewModel.ok_known.collectAsState()

    if (cardsNotEmpty) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = if (cardFace == CardFace.Front) stringResource(id = R.string.question) else stringResource(
                            id = R.string.answer
                        ), fontStyle = FontStyle.Italic
                    )

                    // Which side is front and which is the answer:
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(id = R.string.front_first_card))
                        Spacer(modifier = Modifier.width(10.dp))
                        Switch(checked = frontIsQuestion, onCheckedChange = {
                            frontIsQuestion = it
                        }, thumbContent = if (frontIsQuestion) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        } else {
                            null
                        })
                    }
                }
                Column(
                    Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val configuration = LocalConfiguration.current
                    //val height = configuration.screenHeightDp.dp
                    val width = (configuration.screenWidthDp.dp / 5) * 4
                    Box() {
                        FlipCard(
                            cardFace = cardFace,
                            onClick = { cardFace = cardFace.next },
                            modifier = Modifier
                                .width(width = width)
                                .aspectRatio(ratio = 1f),
                            front = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    card.let {
                                        Text(
                                            text = (if (frontIsQuestion) it.frontSide else it.backSide),
                                            fontSize = 22.5.sp,
                                            modifier = Modifier.verticalScroll(scrollState)
                                        )
                                    }

                                }
                            },
                            back = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    card.let {
                                        Text(
                                            text = (if (frontIsQuestion) it.backSide else it.frontSide),
                                            fontSize = 22.5.sp
                                        )
                                    }
                                }
                            },
                        )
                    }
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .height(125.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    var visible by remember { mutableStateOf(false) }
                    if (cardFace == CardFace.Back) {
                        visible = true
                    } else if (cardFace == CardFace.Front) {
                        visible = false
                    }
                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInVertically(initialOffsetY = { 15 }) + expandVertically(
                            expandFrom = Alignment.Bottom
                        ) + scaleIn(
                            initialScale = 0f, transformOrigin = TransformOrigin(0.75f, 0f)
                        ) + fadeIn(initialAlpha = 0.0f),

                        exit = slideOutVertically(targetOffsetY = { 15 }) + shrinkVertically(
                            shrinkTowards = Alignment.Top
                        ) + scaleOut(
                            transformOrigin = TransformOrigin(0.5f, 0f)
                        ) + fadeOut(
                            targetAlpha = 0f
                        )
                    ) {
                        Row {
                            Column {
                                Button(onClick = {
                                    viewModel.difficult(card, cardsDao, id)
                                    viewModel.addOkKnown()
                                    if (cards.size - 1 > i) {
                                        cardFace = CardFace.Front;
                                        co.launch {
                                            delay(500);
                                            i++
                                            card = cards[i]
                                        }
                                    }
                                }) { Text(text = stringResource(id = R.string.difficulty_difficult)) }

                                Button(onClick = {
                                    // handle difficult card:
                                    viewModel.again(card, cardsDao, id)
                                    cards.add(card)
                                    viewModel.addUnKnown()
                                    Log.d("test", cards.size.toString() + " | " + i.toString())
                                    if (cards.size - 1 > i) {
                                        cardFace = CardFace.Front
                                        co.launch {
                                            delay(500);
                                            i++
                                            card = cards[i]
                                        }

                                    } else {
                                        cardsNotEmpty = false
                                    }
                                    Log.d("test", card.toString())
                                    //card.let { onClick(0, it) }
                                }) { Text(text = stringResource(id = R.string.difficulty_again)) }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Button(onClick = {
                                    viewModel.well(card, cardsDao, id)
                                    viewModel.addOkKnown()
                                    if (cards.size - 1 > i) {
                                        cardFace = CardFace.Front
                                        co.launch {
                                            delay(500);
                                            i++
                                            card = cards[i]
                                        }

                                    } else {
                                        cardsNotEmpty = false
                                    }
                                }) { Text(text = stringResource(id = R.string.difficulty_well)) }

                                Button(onClick = {
                                    viewModel.easy(card, cardsDao, id)
                                    viewModel.addKnown()
                                    if (cards.size - 1 > i) {
                                        cardFace = CardFace.Front
                                        co.launch {
                                            delay(500);
                                            i++
                                            card = cards[i]
                                        }
                                    } else {
                                        cardsNotEmpty = false
                                    }
                                }) { Text(text = stringResource(id = R.string.difficulty_easy)) }
                            }
                        }
                    }
                }
            }
        }
    } else {
        AnalyseScreen(known.value, ok_known.value, unknown.value, navController = navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyseScreen(known: Int, ok_known: Int, unknown: Int, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Results") })
        }
    ){ it
        Column(
            Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally){
            PieChart(
                modifier = Modifier.size(500.dp),
                input = listOf(
                    PieChartInput(
                        color = Color.Green,
                        value = known,
                        description = "Super gekonnt"
                    ),
                    PieChartInput(
                        color = Color(0xffffa500),
                        value = ok_known,
                        description = "naja"
                    ),
                    PieChartInput(
                        color = Color.Red,
                        value = unknown,
                        description = "garnicht gweusst"
                    )
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Box(modifier = Modifier
                    .size(15.dp)
                    .background(Color.Green))
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Well known -> $known")
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Box(modifier = Modifier
                    .size(15.dp)
                    .background(Color(0xffffa500)))
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Ok known -> $ok_known")
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Box(modifier = Modifier
                    .size(15.dp)
                    .background(Color.Red))
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "unknown -> $unknown")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                navController.popBackStack()
            }) {
                Text(text = "Next")
            }
        }
        
    }
    
}

@Preview
@Composable
fun Prev(){
    val navController = rememberNavController()
    AnalyseScreen(known = 5, ok_known = 7, unknown = 2, navController)
}


