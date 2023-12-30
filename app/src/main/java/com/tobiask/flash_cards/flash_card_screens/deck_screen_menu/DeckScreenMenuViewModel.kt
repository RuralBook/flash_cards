package com.tobiask.flash_cards.flash_card_screens.deck_screen_menu

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobiask.flash_cards.models.QuizCards
import com.tobiask.flash_cards.database.Card
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.Deck
import com.tobiask.flash_cards.database.DecksDAO
import com.tobiask.flash_cards.database.StatsDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import kotlin.math.min
import kotlin.random.Random

class DeckScreenMenuViewModel(val dao: DecksDAO, val daoCards: CardsDao, val statsDao: StatsDao, val deckId: Int) :
    ViewModel() {





    val DeckCards = daoCards.getCards(deckId)


    private val _showPopUpAdd = MutableStateFlow(false)
    val showPopUpAdd = _showPopUpAdd.asStateFlow()

    private val _editCard = MutableStateFlow(
        Card(
            front = "",
            back = "",
            dueTo = "",
            difficulty = 0,
            deckId = 0
        )
    )
    val editCard = _editCard.asStateFlow()

    private val _showPopUpEdit = MutableStateFlow(false)
    val showPopUpEdit = _showPopUpEdit.asStateFlow()

    private val _showPopUpEditCard = MutableStateFlow(false)
    val showPopUpEditCard = _showPopUpEditCard.asStateFlow()


    fun editCardValue(card: Card) { _editCard.value = card }

    fun popUpAdd() {_showPopUpAdd.value = !_showPopUpAdd.value }



    fun popUpEdit() {_showPopUpEdit.value = !_showPopUpEdit.value }

    fun popUpEditCard() { _showPopUpEditCard.value = !_showPopUpEditCard.value }

    fun addDeck(deck: Deck) {
        viewModelScope.launch {
            dao.addDeck(deck)
        }
    }

    fun addCard(card: Card) {
        viewModelScope.launch {
            daoCards.addCard(card)
        }
    }

    fun delOneCard(card: Card) {
        viewModelScope.launch {
            daoCards.deleteOneCard(card)
        }
    }

    fun updateCard(card: Card) {
        viewModelScope.launch {
            daoCards.updateCard(card)
        }
    }


    fun converter(cards: List<Card>): List<QuizCards> {
        val quizCards = mutableListOf<QuizCards>()
        val today = LocalDate.now()
        for (card in cards) {
            val date = LocalDate.parse(card.dueTo)
            if (date.isBefore(today) || date.isEqual(today)) {
                val buffer = QuizCards(
                    id = card.id,
                    frontSide = card.front,
                    frontSideImg = card.frontImg,
                    backSide = card.back,
                    backSideImg = card.backImg,
                    oldDifficulty = card.difficulty,
                    difficulty = card.difficulty,
                    difficultyTimes = card.difficultyTimes
                )
                quizCards.add(buffer)
            }
        }
        return quizCards.shuffled()
    }

    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, filename: String) {

        val scaledBitmap = scaleBitmap(bitmap, 1500, 1500 )

        val stream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        val file = File(context.filesDir, filename)
        val outputStream = FileOutputStream(file)
        outputStream.write(byteArray)
        outputStream.close()
    }

    private fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        val scale = min(maxWidth.toFloat() / originalWidth, maxHeight.toFloat() / originalHeight)

        val matrix = Matrix()
        matrix.postScale(scale, scale)

        return Bitmap.createBitmap(bitmap, 0, 0, originalWidth, originalHeight, matrix, true)
    }


    fun loadBitmapFromInternalStorage(context: Context, filename: String): Bitmap? {
        val file = File(context.filesDir, filename)
        return if (file.exists()) {
            BitmapFactory.decodeFile(file.path)
        } else {
            null
        }
    }

    fun deleteImageFromInternalStorage(context: Context, filename: String): Boolean {
        val file = File(context.filesDir, filename)
        return file.delete()
    }

    private var _isFrontImgDisplayed = MutableStateFlow(false)
    val isFrontImgDisplayed = _isFrontImgDisplayed.asStateFlow()

    private var _isBackImgDisplayed = MutableStateFlow(false)
    val isBackImgDisplayed = _isBackImgDisplayed.asStateFlow()

    fun showFrontImg(){
        _isFrontImgDisplayed.value = !_isFrontImgDisplayed.value
    }

    fun showBackImg(){
        _isBackImgDisplayed.value = !_isBackImgDisplayed.value
    }
}