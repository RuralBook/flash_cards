package com.tobiask.flash_cards.flash_card_screens.exportImportScreen

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobiask.flash_cards.R
import com.tobiask.flash_cards.database.Card
import com.tobiask.flash_cards.database.CardsDao
import com.tobiask.flash_cards.database.DecksDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
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

class ExportImportScreenViewModel(
    val id: Int,
    val dao: CardsDao,
    val decksDAO: DecksDAO,
    val context: Context
) : ViewModel() {

    private val _cardsToImport = MutableStateFlow(mutableListOf<Card>())
    val cardsToImport = _cardsToImport.asStateFlow()

    fun export() {
        val json = JSONObject()
        viewModelScope.launch {
            val cards = dao.getCardsList(id)
            json.put("Cards", addCards(cards))
            saveJson(json.toString())
            Toast.makeText(context, "Saved Json-Backup to Downloads Folder", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addCards(cards: List<Card>): JSONArray {
        Log.d("cards:", "$cards")
        val obj = JSONArray()
        cards.forEach {
            obj.put(
                JSONObject()
                    .put("front", it.front)
                    .put("back", it.back)
            )
        }
        Log.d("object:", "$obj")
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
        var pre = ""
        viewModelScope.launch {
            runBlocking {
               pre = decksDAO.getDeckName(id)[0]
            }
        }
        val fileName = "$pre-${LocalDate.now()}"
        return File.createTempFile(
            fileName,
            ".json",
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        )
    }

    /* IMPORT:
    * get File URI
    * check for Json
    * try to import
    */

    fun import(uri: Uri) {
        try {
            readJsonFile(context, uri)
        } catch (e: IOException) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun isJsonFile(uri: Uri): Boolean {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())

        // Check if the file extension corresponds to JSON
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
        return mimeType?.equals("application/json", ignoreCase = true) == true
    }

    private fun readJsonFile(context: Context, uri: Uri): String {
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

        val cards = jsonObject.getJSONArray("Cards")
        try {
            for (i in 0 until cards.length()) {
                val card = Card(
                    front = cards.getJSONObject(i).getString("front"),
                    back = cards.getJSONObject(i).getString("back"),
                    deckId = id,
                    dueTo = LocalDate.now().toString()
                )
                //_cardsToImport.value.plus(card)
                viewModelScope.launch {
                    dao.addCard(card)
                }
            }
        } catch (e: JSONException) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }

        return stringBuilder.toString()
    }
}