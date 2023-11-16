package com.tobiask.flash_cards.flash_card_screens.training_quiz_screen

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tobiask.flash_cards.QuizCards
import com.tobiask.flash_cards.R
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.DecksDAO
import com.tobiask.flash_cards.flash_card_screens.deck_screen_menu.DeckScreenMenuViewModel
import com.tobiask.flash_cards.flash_card_screens.quiz_screen.CardFace
import com.tobiask.flash_cards.flash_card_screens.quiz_screen.FlipCard


@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingQuizScreen(id: Int, dao: CardsDao, dao1: DecksDAO) {

    val viewModel = viewModel<DeckScreenMenuViewModel>(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DeckScreenMenuViewModel(daoCards = dao, dao = dao1, deckId = id) as T
        }
    })

    val cards = viewModel.converterTrainer(viewModel.cards.collectAsState(initial = emptyList()).value)

    if (cards.isNotEmpty()){

        Log.d("cardSize", cards.size.toString())


        Scaffold(topBar = {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth(1f)){
                Text(text = "TRAINER", fontSize = 30.sp )
            }
        }) {
            Column(
                Modifier
                    .padding(it)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                QuizCard(cards)
            }
        }
    } else{
        EmptyDeck()
    }
}


@SuppressLint("CoroutineCreationDuringComposition", "UnrememberedMutableState")
@Composable
fun QuizCard(
    //id: Int,
    cards: MutableList<QuizCards>,
    //viewModel: DeckScreenMenuViewModel,
    //cardsDao: CardsDao,
) {
    var cardFace by remember {
        mutableStateOf(CardFace.Front)
    }
    var i by remember{ mutableIntStateOf(0) }
    Log.d("test", i.toString())
    var card by remember { mutableStateOf(cards[i])}

    val scrollState = rememberScrollState()

    var frontIsQuestion by remember { mutableStateOf(true) }


    var cardsNotEmpty by remember { mutableStateOf(true)}
    if (cards.size == 0){
        cardsNotEmpty = false
    }
    if (cardsNotEmpty){
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
                                    .fillMaxSize()
                                    .padding(5.dp),
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
                                    .fillMaxSize()
                                    .padding(5.dp),
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
                                if (cards.size - 1 > i) {
                                    i++
                                    card = cards[i]
                                    cardFace = CardFace.Front
                                }else {cardsNotEmpty = false}
                            }) { Text(text = stringResource(id = R.string.next)) }
                        }
                    }
                }
            }
        }
        }
    }
    else{
        EmptyDeck()
    }
}

@Composable
fun EmptyDeck() {
    Text(
        text = stringResource(id = R.string.empty_deck),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 31.sp
    )
}


