package com.tobiask.flash_cards.flash_card_screens.settings_screen

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobiask.flash_cards.database.Card
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.Deck
import com.tobiask.flash_cards.database.DecksDAO
import com.tobiask.flash_cards.database.Folder
import com.tobiask.flash_cards.database.FolderDao
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.Writer
import java.time.LocalDate

class SettingsScreenViewModel(
    val dao: CardsDao,
    private val decksDAO: DecksDAO,
    private val folderDao: FolderDao,
    val context: Context
) : ViewModel() {


    fun exportDatabase() {
        val json = JSONObject()
        viewModelScope.launch {
            runBlocking {
                json.put("Folders", addFolders())
                    .put("Root-Decks", addDecks(0))
                saveJson(json.toString())
            }
        }
    }

    private suspend fun addFolders(): JSONArray {
        val arr = JSONArray()
        val folders = folderDao.getAllFoldersStatic()
        folders.forEach {
            arr.put(
                JSONObject()
                    .put("name", it.name)
                    .put("decks:", addDecks(it.id))
            )
        }
        Log.d("folders", "$arr")
        return arr
    }

    private suspend fun addDecks(folderId: Int): JSONArray {
        val arr = JSONArray()
        val decks = decksDAO.getAllDecksWithParentStatic(folderId)

        decks.forEach {
            arr.put(
                JSONObject()
                    .put("name", it.name)
                    .put("cards", addCards(it.id))
            )
        }
        return arr
    }

    private suspend fun addCards(deckId: Int): JSONArray {
        val obj = JSONArray()
        val cards = dao.getCardsList(deckId)
        cards.forEach {
            obj.put(
                JSONObject()
                    .put("front", it.front)
                    .put("back", it.back)
            )
        }
        return obj
    }

    private fun saveJson(jsonString: String) {
        val output: Writer
        val file = createFile()
        output = BufferedWriter(FileWriter(file))
        output.write(jsonString)
        output.close()
    }

    private fun createFile(): File {
        val pre = "FullExport"
        val fileName = "$pre-${LocalDate.now()}"
        return File.createTempFile(
            fileName,
            ".json",
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        )
    }

//READ EXPORT
//----------------------------------------------------------------------------------------------

    fun import(uri: Uri) {
        viewModelScope.launch {
            runBlocking {
                val cards = dao.getAllCardsStatic()
                val decks = decksDAO.getAllDecksStatic()
                val folders = folderDao.getAllFoldersStatic()
                if (cards.isEmpty() and folders.isEmpty() and decks.isEmpty()){
                    try {
                        readJsonFile(context, uri)
                    } catch (e: IOException) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                    }
                } else {

                }
            }
        }
    }

    private suspend fun readJsonFile(context: Context, uri: Uri): String {
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String? = reader.readLine()
        while (line != null) {
            stringBuilder.append(line).append("\n")
            line = reader.readLine()
        }

        // Close
        reader.close()
        inputStream?.close()

        // convert to JsonObject
        val jsonData = stringBuilder.toString()
        val jsonObject = JSONObject(jsonData)
        try {
            val folders = jsonObject.getJSONArray("Folders")
            saveFolders(folders)
            val rootDeck =jsonObject.getJSONArray("Root-Decks")
            saveDecks(rootDeck, 0)
        } catch (e: JSONException) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }
        return stringBuilder.toString()
    }


    private suspend fun saveFolders(folders: JSONArray) {
        for (i in 0 until folders.length()) {
            val folder = Folder(
                id = folderDao.getNextFolderId(), // Get the next available folder ID
                name = folders.getJSONObject(i).getString("name")
            )
            viewModelScope.launch {
                runBlocking {
                    folderDao.insertFolder(folder)
                }
            }
            val decks = folders.getJSONObject(i).getJSONArray("decks:")
            saveDecks(decks, folder.id)
        }
    }

    private suspend fun saveDecks(decks: JSONArray, id: Int) {
        for (i in 0 until decks.length()) {
            val deck = Deck(
                id = decksDAO.getNextDeckId(), // Get the next available deck ID
                name = decks.getJSONObject(i).getString("name"),
                parentFolder = id
            )
            decksDAO.addDeck(deck)
            val cards = decks.getJSONObject(i).getJSONArray("cards")
            saveCards(cards, deck.id)
        }
    }

    private suspend fun saveCards(cards: JSONArray, id: Int) {
        for (i in 0 until cards.length()) {
            val card = Card(
                front = cards.getJSONObject(i).getString("front"),
                back = cards.getJSONObject(i).getString("back"),
                deckId = id,
                dueTo = LocalDate.now().toString()
            )
            dao.addCard(card)
        }
    }
}