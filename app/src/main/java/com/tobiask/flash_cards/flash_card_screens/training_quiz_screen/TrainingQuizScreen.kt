package com.tobiask.flash_cards.flash_card_screens.training_quiz_screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tobiask.flash_cards.R
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.StatsDao
import com.tobiask.flash_cards.ui_elements.CardFace
import com.tobiask.flash_cards.ui_elements.FlipCard
import com.tobiask.flash_cards.flash_card_screens.quiz_screen.QuizScreenViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText


@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingQuizScreen(id: Int, dao: CardsDao, statsDao: StatsDao) {

    val viewModel =
        viewModel<QuizScreenViewModel>(factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return QuizScreenViewModel(
                    dao,
                    statsDao,
                    id
                ) as T
            }
        })

    val cards by remember {
        mutableStateOf(viewModel.trainingCards)
    }

    val showFrontImage = viewModel.isFrontImgDisplayed.collectAsState().value
    val showBackImage = viewModel.isBackImgDisplayed.collectAsState().value

    if (cards.isNotEmpty()) {
        Scaffold(topBar = {}) { it ->
            Column(
                Modifier
                    .padding(it)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var i by remember {
                    mutableIntStateOf(0)
                }

                var card = cards[i]

                var cardFace by remember {
                    mutableStateOf(CardFace.Front)
                }

                val scrollStateFront = rememberScrollState()
                val scrollStateBack = rememberScrollState()

                var frontIsQuestion by remember { mutableStateOf(true) }


                var cardsNotEmpty by remember { mutableStateOf(true) }
                if (cards.isEmpty()) {
                    cardsNotEmpty = false
                }

                if (showFrontImage){
                    com.tobiask.flash_cards.flash_card_screens.quiz_screen.ShowImage(
                        viewModel = viewModel,
                        filename = card.frontSideImg,
                        front = true
                    )
                }

                if (showBackImage){
                    com.tobiask.flash_cards.flash_card_screens.quiz_screen.ShowImage(
                        viewModel = viewModel,
                        filename = card.backSideImg,
                        front = false
                    )
                }


                if (cardsNotEmpty) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
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
                                Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
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
                                                    .fillMaxSize(),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Column(
                                                    Modifier
                                                        .fillMaxSize()
                                                        .padding(5.dp)
                                                        .scrollable(
                                                            scrollStateFront,
                                                            Orientation.Vertical
                                                        ),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    card.let {
                                                        MarkdownText(
                                                            fontResource = R.font.nunito_regular,
                                                            markdown = (if (frontIsQuestion) it.frontSide else it.backSide),
                                                            fontSize = 17.5.sp,
                                                            color = MaterialTheme.colorScheme.onBackground
                                                        )
                                                    }
                                                    if (card.frontSideImg != "") {
                                                        Spacer(modifier = Modifier.height(20.dp))
                                                        Button(onClick = { viewModel.showFrontImg() }) {
                                                            Text(text = stringResource(id = R.string.show_image))
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        back = {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize(),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                if (cardFace == CardFace.Back) {
                                                    Column(
                                                        Modifier
                                                            .fillMaxSize()
                                                            .padding(5.dp)
                                                            .scrollable(
                                                                scrollStateBack,
                                                                Orientation.Vertical
                                                            ),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        card.let {
                                                            MarkdownText(
                                                                markdown = (if (frontIsQuestion) it.backSide else it.frontSide),
                                                                fontSize = 17.5.sp,
                                                                color = MaterialTheme.colorScheme.onBackground
                                                            )
                                                        }
                                                        if (card.backSideImg != "") {
                                                            Spacer(modifier = Modifier.height(20.dp))
                                                            Button(onClick = { viewModel.showBackImg() }) {
                                                                Text(text = stringResource(id = R.string.show_image))
                                                            }
                                                        }
                                                    }
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
                                        expandFrom = Alignment.Top
                                    ) + scaleIn(
                                        initialScale = 0f,
                                        transformOrigin = TransformOrigin(0.75f, 0f)
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
                                                    cardFace = CardFace.Front;
                                                    i++
                                                    card = cards[i]
                                                } else {
                                                    cardsNotEmpty = false
                                                }
                                            }) { Text(text = stringResource(id = R.string.next)) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    EmptyDeck()
                }
            }
        }
    } else {
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


