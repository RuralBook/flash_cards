package com.tobiask.flash_cards.flash_card_screens.deck_screen_menu

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.ModelTraining
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.rememberModalBottomSheetState
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.getString
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tobiask.flash_cards.R
import com.tobiask.flash_cards.database.Card
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.Deck
import com.tobiask.flash_cards.database.DecksDAO
import com.tobiask.flash_cards.database.StatsDao
import com.tobiask.flash_cards.flash_card_screens.quiz_screen.QuizScreenViewModel
import com.tobiask.flash_cards.navigation.Screen
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.flow.asFlow
import java.io.File
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DeckScreenMenu(
    dao: DecksDAO,
    daoCard: CardsDao,
    statsDao: StatsDao,
    id: Int,
    navController: NavController
) {

    val context = LocalContext.current

    val viewModel =
        viewModel<DeckScreenMenuViewModel>(factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DeckScreenMenuViewModel(dao, daoCard, statsDao, id) as T
            }
        })
    val deck = viewModel.dao.getDeck(id).collectAsState(initial = Deck(0, ""))
    val popUpEdit by viewModel.showPopUpEdit.collectAsState()

    val cards = viewModel.DeckCards.collectAsState(initial = emptyList())


    if (popUpEdit) {
        EditDeck(
            viewModel = viewModel, context = context, deck.value
        )
    }

    val popUpAdd by viewModel.showPopUpAdd.collectAsState()
    if (popUpAdd) {
        AddCard(viewModel = viewModel, deck = deck.value)
    }

    Scaffold(topBar = {
        Column(
            Modifier
                .padding(top = 10.dp, end = 10.dp, start = 10.dp)
                .clip(shape = RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = deck.value.name,
                    style = MaterialTheme.typography.headlineLarge,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.nunito_bold))
                )
            }
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Box(Modifier.clickable {
                    val cards = viewModel.converter(cards.value)
                    if (cards.isNotEmpty()) {
                        val ids = deck.value.id
                        val routeWithArgs = "${Screen.QuizScreen.route}?id=${ids}"
                        navController.navigate(routeWithArgs)
                    } else {
                        Toast.makeText(
                            context,
                            getString(context, R.string.nothing_to_learn),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleOutline,
                        contentDescription = null,
                        Modifier.size(35.dp, 35.dp)
                    )
                }
                Box(Modifier.clickable {
                    val ids = deck.value.id
                    val routeWithArgs = "${Screen.TestQuizScreen.route}?id=${ids}"
                    navController.navigate(routeWithArgs)
                }) {
                    Icon(
                        imageVector = Icons.Default.ModelTraining,
                        contentDescription = null,
                        Modifier.size(35.dp, 35.dp)
                    )
                }
                Box(Modifier.clickable {
                    val ids = deck.value.id
                    val routeWithArgs = "${Screen.ExportImportScreen.route}?id=${ids}"
                    navController.navigate(routeWithArgs)
                }) {
                    Icon(
                        imageVector = Icons.Default.ImportExport,
                        contentDescription = null,
                        Modifier.size(35.dp, 35.dp)
                    )
                }
            }
        }
    }, floatingActionButton = {
        Column() {
            FloatingActionButton(onClick = { viewModel.popUpEdit() }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
            Spacer(modifier = Modifier.height(10.dp))
            FloatingActionButton(onClick = { viewModel.popUpAdd() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    }, content = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    end = 20.dp,
                    start = 20.dp,
                    top = it.calculateTopPadding(),
                    bottom = it.calculateBottomPadding()
                )
        ) {
            LazyColumn(
                Modifier.padding(top = 15.dp)
            ) {
                itemsIndexed(
                    cards.value,
                    key = { _, card ->
                        card.id
                    }
                ) { _, row ->
                    RevealSwipe(modifier = Modifier.padding(top = 5.dp, bottom = 15.dp),
                        directions = setOf(
                            RevealDirection.EndToStart,
                            RevealDirection.StartToEnd
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
                        },
                        hiddenContentStart = {
                            Icon(
                                modifier = Modifier.padding(horizontal = 25.dp),
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        },
                        backgroundCardStartColor = MaterialTheme.colorScheme.primary,
                        onBackgroundStartClick = {
                            viewModel.editCardValue(row)
                            viewModel.popUpEditCard()
                        }
                    ) {
                        CardCardDeckScreen(card = row, viewModel, context)
                    }
                }
            }
        }
    })
}

@Composable
fun CardCardDeckScreen(card: Card, viewModel: DeckScreenMenuViewModel, context: Context) {

    val scroll = rememberScrollState()
    val showFrontImage = viewModel.isFrontImgDisplayed.collectAsState().value
    val showBackImage = viewModel.isBackImgDisplayed.collectAsState().value

    if (showFrontImage) {
        ShowImage(
            viewModel = viewModel,
            filename = card.frontImg,
            front = true
        )
    }

    if (showBackImage) {
        ShowImage(
            viewModel = viewModel,
            filename = card.backImg,
            front = false
        )
    }

    val popUpEditCard by viewModel.showPopUpEditCard.collectAsState()

    if (popUpEditCard) {
        EditCard(
            viewModel = viewModel, context = context
        )
    }

    var front by remember {
        mutableStateOf(true)
    }

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
                .clickable { front = !front }

        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (front) {
                        MarkdownText(
                            fontResource = R.font.nunito_regular,
                            modifier = Modifier
                                .padding(10.dp),
                            markdown = card.front,
                            fontSize = 20.sp,
                            onClick = { front = false },
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    } else {
                        MarkdownText(
                            fontResource = R.font.nunito_italic,
                            modifier = Modifier
                                .padding(10.dp),
                            markdown = card.back,
                            fontSize = 20.sp,
                            onClick = { front = true },
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                if (front) {
                    if (card.frontImg != "") {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            IconButton(onClick = { viewModel.showFrontImg() }) {
                                Icon(imageVector = Icons.Default.Image, contentDescription = null)
                            }
                        }
                    }
                } else {
                    if (card.backImg != "") {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            IconButton(onClick = { viewModel.showBackImg() }) {
                                Icon(imageVector = Icons.Default.Image, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}

// POP-UPs
//--------------------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDeck(viewModel: DeckScreenMenuViewModel, context: Context, deck: Deck) {
    val textState = remember { mutableStateOf(TextFieldValue(deck.name)) }
    val configuration = LocalConfiguration.current
    val height = (configuration.screenHeightDp.dp / 4) * 1
    Dialog(onDismissRequest = {
        viewModel.popUpEdit()
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
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = stringResource(id = R.string.edit_deck),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                OutlinedTextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    label = { Text(text = stringResource(id = R.string.name_of_the_deck)) },
                    maxLines = 2
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = {
                        if (textState.value.text.isNotEmpty()) {
                            viewModel.popUpEdit()
                            viewModel.addDeck(
                                Deck(
                                    name = textState.value.text,
                                    id = deck.id,
                                    parentFolder = deck.parentFolder,
                                    important = deck.important
                                )
                            )
                        } else {
                            Toast.makeText(
                                context,
                                R.string.please_enter_a_name,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }) {
                        Text(text = stringResource(id = R.string.update))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCard(viewModel: DeckScreenMenuViewModel, deck: Deck) {
    var isAddingFront by remember {
        mutableStateOf(true)
    }
    val textState = remember { mutableStateOf(TextFieldValue(text = "# ")) }
    val textState1 = remember { mutableStateOf(TextFieldValue(text = "# ")) }
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val height = (configuration.screenHeightDp.dp / 5) * 2 // 4 = 5?? TODO (V1.3.5): Improve space
    var frontImage: String by remember {
        mutableStateOf("")
    }
    val frontImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            val text = viewModel.getRandomString(16)
            viewModel.saveBitmapToInternalStorage(context = context, bitmap, text)

            if (viewModel.loadBitmapFromInternalStorage(context, text) != null) {
                frontImage = text
            }
        }
    }
    var backImage: String by remember {
        mutableStateOf("")
    }
    val backImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri);
            val text = viewModel.getRandomString(16)
            viewModel.saveBitmapToInternalStorage(context = context, bitmap, text)

            if (viewModel.loadBitmapFromInternalStorage(context, text) != null) {
                backImage = text
            }
        }
    }

    Dialog(onDismissRequest = {
        viewModel.popUpAdd()
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
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = stringResource(id = R.string.new_card),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                if (isAddingFront) {
                    Column(verticalArrangement = Arrangement.SpaceBetween) {
                        OutlinedTextField(
                            value = textState.value,
                            onValueChange = { textState.value = it },
                            label = { Text(text = stringResource(id = R.string.fronside)) },
                            maxLines = 3,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row {
                            IconButton(onClick = {
                                frontImageLauncher.launch(
                                    PickVisualMediaRequest(
                                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }) {
                                Icon(
                                    imageVector = if (frontImage == "") Icons.Default.Image else Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.SpaceBetween) {
                        OutlinedTextField(
                            value = textState1.value,
                            onValueChange = { textState1.value = it },
                            label = { Text(text = stringResource(id = R.string.backside)) },
                            maxLines = 3,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row {
                            IconButton(
                                onClick = {
                                    backImageLauncher.launch(
                                        PickVisualMediaRequest(
                                            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                }) {
                                Icon(
                                    imageVector = if (backImage == "") Icons.Default.Image else Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (!isAddingFront) {
                        IconButton(onClick = { isAddingFront = true }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                    Button(
                        onClick = {
                            if (isAddingFront) {
                                isAddingFront = false
                            } else if (textState.value.text != "# " || textState.value.text != "#" || textState.value.text.isNotBlank() && textState1.value.text != "# " || textState1.value.text != "#" || textState1.value.text.isNotBlank()) {
                                viewModel.popUpAdd()
                                viewModel.addCard(
                                    Card(
                                        front = textState.value.text,
                                        frontImg = frontImage,
                                        back = textState1.value.text,
                                        backImg = backImage,
                                        deckId = deck.id,
                                        folderRoute = 0,
                                        dueTo = LocalDate.now().toString()
                                    )
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.please_enter_a_name,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }) {
                        Icon(
                            imageVector = if (isAddingFront) {
                                Icons.Default.ArrowForward
                            } else {
                                Icons.Default.Check
                            },
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCard(viewModel: DeckScreenMenuViewModel, context: Context) {
    var isAddingFront by remember {
        mutableStateOf(true)
    }
    val card = viewModel.editCard.collectAsState().value
    val textState = remember { mutableStateOf(TextFieldValue(card.front)) }
    val textState1 = remember { mutableStateOf(TextFieldValue(card.back)) }
    var resetDifficulty by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val height = (configuration.screenHeightDp.dp / 5) * 2 // TODO (V1.3.5): Improve space

    var frontImage: String by remember {
        mutableStateOf(card.frontImg)
    }
    val frontImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri);
            if (textState.value.text.isNotBlank()) {
                val text = viewModel.getRandomString(16)
                viewModel.saveBitmapToInternalStorage(context = context, bitmap, text)

                if (viewModel.loadBitmapFromInternalStorage(context, text) != null) {
                    frontImage = text
                }
            }
        }
    }
    var backImage: String by remember {
        mutableStateOf(card.backImg)
    }
    val backImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri);
            if (textState1.value.text.isNotBlank()) {
                val text = viewModel.getRandomString(16)
                viewModel.saveBitmapToInternalStorage(context = context, bitmap, text)

                if (viewModel.loadBitmapFromInternalStorage(context, text) != null) {
                    backImage = text
                }
            }
        }
    }

    Dialog(onDismissRequest = {
        viewModel.popUpEditCard()
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
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = stringResource(id = R.string.edit_card),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                if (isAddingFront) {
                    Column(verticalArrangement = Arrangement.SpaceBetween) {
                        OutlinedTextField(
                            value = textState.value,
                            onValueChange = { textState.value = it },
                            label = { Text(text = stringResource(id = R.string.fronside)) },
                            maxLines = 3,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row {
                            IconButton(onClick = {
                                frontImageLauncher.launch(
                                    PickVisualMediaRequest(
                                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }) {
                                Icon(
                                    imageVector = if (frontImage == "") Icons.Default.Image else Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                            if (frontImage != "") {
                                Button(onClick = {
                                    viewModel.deleteImageFromInternalStorage(context, frontImage)
                                    frontImage = ""
                                }) {
                                    Text(text = stringResource(id = R.string.delete_image))
                                }
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.SpaceBetween) {
                        OutlinedTextField(
                            value = textState1.value,
                            onValueChange = { textState1.value = it },
                            label = { Text(text = stringResource(id = R.string.backside)) },
                            maxLines = 3,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row {
                            IconButton(
                                onClick = {
                                    backImageLauncher.launch(
                                        PickVisualMediaRequest(
                                            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                }) {
                                Icon(
                                    imageVector = if (backImage == "") Icons.Default.Image else Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                            if (backImage != "") {
                                Button(onClick = {
                                    viewModel.deleteImageFromInternalStorage(context, backImage)
                                    backImage = ""
                                }) {
                                    Text(text = stringResource(id = R.string.delete_image))
                                }
                            }
                        }
                    }
                }
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = stringResource(id = R.string.reset_difficulty))
                    Switch(checked = resetDifficulty, onCheckedChange = { resetDifficulty = it })
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (!isAddingFront) {
                        IconButton(onClick = { isAddingFront = true }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                    Button(onClick = {
                        if (isAddingFront) {
                            isAddingFront = false
                        } else if (textState.value.text.isNotEmpty() && textState1.value.text.isNotEmpty()) {
                            val updateCard = Card(
                                id = card.id,
                                deckId = card.deckId,
                                front = textState.value.text,
                                frontImg = frontImage,
                                back = textState1.value.text,
                                backImg = backImage,
                                folderRoute = 0,
                                difficulty = if (!resetDifficulty) card.difficulty else 0,
                                difficultyTimes = if (!resetDifficulty) card.difficultyTimes else 0,
                                dueTo = if (!resetDifficulty) card.dueTo else LocalDate.now()
                                    .toString()
                            )
                            viewModel.updateCard(
                                updateCard
                            )
                            viewModel.popUpEditCard()
                        } else {
                            Toast.makeText(
                                context, R.string.please_enter_a_valid_text, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        if (isAddingFront) {
                            Text(text = stringResource(id = R.string.next))
                        } else {
                            Text(text = stringResource(id = R.string.update))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowImage(viewModel: DeckScreenMenuViewModel, filename: String, front: Boolean) {
    val context = LocalContext.current
    val image =
        viewModel.loadBitmapFromInternalStorage(context, filename)
    Dialog(onDismissRequest = { if (front) viewModel.showFrontImg() else viewModel.showBackImg() }) {
        Card() {
            if (image != null) {
                Image(bitmap = image.asImageBitmap(), contentDescription = null)
            }
        }
    }
}