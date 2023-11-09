package com.tobiask.flash_cards.screens.quiz_screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

enum class CardFace(val angle: Float) {
    Front(0f) {
        override val next: CardFace
            get() = Back
    },
    Back(180f) {
        override val next: CardFace
            get() = Front
    };

    abstract val next: CardFace
}

@Composable
fun TestQuiz(){
    var cardFace by remember {
    mutableStateOf(CardFace.Front)
}

    Box(modifier = Modifier.fillMaxSize()){
        FlipCard(
            cardFace = cardFace,
            onClick = { cardFace = cardFace.next },
            modifier = Modifier
                .fillMaxWidth(.5f)
                .aspectRatio(1f),
            front = {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Red),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Front",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }
            },
            back = {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Blue),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Back",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
            },
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlipCard(
    cardFace: CardFace,
    onClick: (CardFace) -> Unit,
    modifier: Modifier = Modifier,
    back: @Composable () -> Unit = {},
    front: @Composable () -> Unit = {},
) {
    val rotation = animateFloatAsState(
        targetValue = cardFace.angle,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing,
        ), label = ""
    )
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        onClick = { onClick(cardFace) },
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 12f * density
            },
    ) {
        if (rotation.value <= 90f) {
            Box(
                Modifier.fillMaxSize()
            ) {
                front()
            }
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = -180f
                    },
            ) {
                back()
            }
        }
    }
}
