package com.tobiask.flash_cards.screens.quiz_screen

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.material.swipeable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.tobiask.flash_cards.QuizCards
import com.tobiask.flash_cards.R
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.navigation.Screen
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.sql.Time
import kotlin.concurrent.timer


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(id: Int, dao: CardsDao, navController: NavController) {
    val context = LocalContext.current
    val viewModel = viewModel<QuizScreenViewModel>(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return QuizScreenViewModel(dao) as T
        }
    })

    val inserted = remember {
        mutableStateOf(false)
    }
    val cardsOld = viewModel.dao.getCards(id).collectAsState(initial = emptyList())
    val cards1 = viewModel.getQuizCards(cards = cardsOld.value)
    val cards = viewModel.converter(cards1)
    if (cards.isNotEmpty()) {
        inserted.value = true
    }
    System.gc()


    Scaffold(topBar = {}) {
        Column(
            Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QuizCard(id, cards.toMutableList(), navController, viewModel, dao)
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun QuizCard(
    id: Int,
    cards: MutableList<QuizCards?>,
    navController: NavController,
    viewModel: QuizScreenViewModel,
    cardsDao: CardsDao
) {
    var size = cards.size
    val i = remember { mutableIntStateOf(0) }


    var cardFace by remember {
        mutableStateOf(CardFace.Front)
    }

    val scrollState = rememberScrollState()

    var frontIsQuestion by remember { mutableStateOf(true) }


    if (size > 0 && i.intValue < size) {
        var card = cards[i.intValue]
        var offsetX by remember { mutableFloatStateOf(0f) }

        /*if (imgPopUpFront.value){
            if (card != null) {
                card.frontSideImg?.let { DisplayFrontImage(img = it, viewModel = viewModel) }
            }
        }*/

        /*if (imgPopUpBack.value){
            if (card != null) {
                card.backSideImg?.let { displayImage(img = it, viewModel = viewModel) }
            }
        }*/

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

                    // Whitch side is front and which is the answer:
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
                    val width = (configuration.screenWidthDp.dp / 5) * 4/*ElevatedCard(
                        Modifier.size(300.dp, height / 2),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Box(Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTransformGestures { _, panGesture, _, _ ->
                                    offsetX += panGesture.component1()

                                    // Update content based on the direction of swipe
                                    front.value = offsetX > 0
                                }
                            }
                        ) {
                            Column(
                                Modifier
                                    .padding(10.dp)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (front.value) {
                                    card?.let {
                                        Text(
                                            text = if (frontIsQuestion) it.frontSide else it.backSide,
                                            fontSize = 22.5.sp,
                                            modifier = Modifier.verticalScroll(scrollState)
                                        )
                                        Log.e("image", card!!.frontSideImg.toString())
                                    }

                                } else {
                                    card?.let {
                                        Text(
                                            text = if (frontIsQuestion) it.backSide else it.frontSide,
                                            fontSize = 22.5.sp
                                        )
                                        Log.e("image", card!!.frontSideImg.toString())
                                        if (card!!.backSideImg != null) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Button(onClick = { viewModel.popUpImgBack() }) {
                                                Text(text = "Bild")
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }*/



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
                                    card?.let {
                                        Text(
                                            text = if (frontIsQuestion) it.frontSide else it.backSide,
                                            fontSize = 22.5.sp,
                                            modifier = Modifier.verticalScroll(scrollState)
                                        )
                                        Log.e("image", card!!.frontSideImg.toString())
                                    }

                                }
                            },
                            back = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.background),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    card?.let {
                                        Text(
                                            text = if (frontIsQuestion) it.backSide else it.frontSide,
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

                                    cards[i.intValue] = card?.let {
                                        viewModel.dificult(
                                            it, dao = cardsDao, id
                                        )
                                    }
                                    cards[i.intValue] = null
                                    if (i.intValue < size - 1) {
                                        i.intValue++
                                        cardFace = CardFace.Front
                                    } else {
                                        val routeWithArgs = "${Screen.DeckScreen.route}?id=${id}"
                                        navController.navigate(routeWithArgs)
                                    }
                                }) { Text(text = stringResource(id = R.string.difficulty_difficult)) }

                                Button(onClick = {
                                    // handle difficult card:
                                    card = card?.let { viewModel.again(it, cardsDao, id) }
                                    card?.let { c -> cards.add(c) } // Add the card back to the list
                                    size = cards.size // Update the list size
                                    cards[i.intValue] = null
                                    if (i.intValue < size - 1) {
                                        i.intValue++
                                        cardFace = CardFace.Front
                                    } else {
                                        val routeWithArgs = "${Screen.DeckScreen.route}?id=${id}"
                                        navController.navigate(routeWithArgs)
                                    }
                                }) { Text(text = stringResource(id = R.string.difficulty_again)) }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Button(onClick = {
                                    card?.let { viewModel.well(it, dao = cardsDao, id) }
                                    cards[i.intValue] = null
                                    if (i.intValue < size - 1) {
                                        i.intValue++
                                        cardFace = CardFace.Front
                                    } else {
                                        val routeWithArgs = "${Screen.DeckScreen.route}?id=${id}"
                                        navController.navigate(routeWithArgs)
                                    }
                                }) { Text(text = stringResource(id = R.string.difficulty_well)) }

                                Button(onClick = {
                                    card?.let { viewModel.easy(it, dao = cardsDao, id) }
                                    cards[i.intValue] = null // just to save memory
                                    if (i.intValue < size - 1) {
                                        i.intValue++
                                        cardFace = CardFace.Front
                                    }
                                }) { Text(text = stringResource(id = R.string.difficulty_easy)) }
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

@Composable
fun EmptyDeck(){
    Text(text = stringResource(id = R.string.empty_deck), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 31.sp)
}


