package com.tobiask.flash_cards

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.tobiask.flash_cards.database.FlashCardsDatabase
import com.tobiask.flash_cards.navigation.Screen
import com.tobiask.flash_cards.flash_card_screens.folder_screen_menu.FolderScreenMenu
import com.tobiask.flash_cards.flash_card_screens.main_screen.MainScreen
import com.tobiask.flash_cards.flash_card_screens.quiz_screen.QuizScreen
import com.tobiask.flash_cards.flash_card_screens.training_quiz_screen.TrainingQuizScreen
import com.tobiask.flash_cards.flash_card_screens.deck_screen_menu.DeckScreenMenu
import com.tobiask.flash_cards.flash_card_screens.exportImportScreen.ExportImportScreen
import com.tobiask.flash_cards.flash_card_screens.onboarding_screen.OnboardingPage
import com.tobiask.flash_cards.flash_card_screens.settings_screen.SettingsScreen
import com.tobiask.flash_cards.flash_card_screens.statistics_screen.StatisticsScreen
import com.tobiask.flash_cards.ui.theme.Flash_cardsTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private val request =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted = permissions.values.reduce { acc, b -> acc && b }
            if (!isGranted) {
                runBlocking {
                    Toast.makeText(this@MainActivity, "In Order To Let this app Work those Permissions are nessesary", Toast.LENGTH_LONG).show()
                    finish()
                }

            }
        }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Flash_cardsTheme {
                val context = LocalContext.current
                val db by lazy {
                    Room.databaseBuilder(
                        context = context,
                        FlashCardsDatabase::class.java,
                        "FlashCards.db"
                    ).fallbackToDestructiveMigration().build()
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val allPermissionsGranted by remember {
                        mutableStateOf(hasRequiredPermissions())
                    }
                    if (!allPermissionsGranted){
                        request.launch(REQUIREDPERMISSIONS)
                    }
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Screen.MainScreen.route){
                        composable(route = Screen.MainScreen.route) {
                            MainScreen(navController, db.decksDao, db.folderDao, db.cardsDao, db.statsDao)
                        }
                        composable(route = Screen.OnboardingScreen.route){
                            OnboardingPage(navController)
                        }
                        composable(route = "${Screen.DeckScreen.route}?id={id}" ,arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                                defaultValue = 0
                                nullable = false
                            }
                        )
                        ) { entry ->
                            val id = entry.arguments?.getInt("id") ?: 0
                            DeckScreenMenu(dao = db.decksDao, daoCard = db.cardsDao,id = id, statsDao = db.statsDao, navController =  navController)
                        }
                        composable(route = "${Screen.FolderScreen.route}?id={id}" ,arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                                defaultValue = 0
                                nullable = false
                            }
                        )
                        ) { entry ->
                            val id = entry.arguments?.getInt("id") ?: 0
                            FolderScreenMenu(
                                dao = db.decksDao,
                                daoFolder = db.folderDao,
                                cardsDao = db.cardsDao ,
                                id = id,
                                navController = navController
                            )
                        }
                        composable(route = "${Screen.QuizScreen.route}?id={id}" ,arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                                defaultValue = 0
                                nullable = false
                            }
                        )
                        ) { entry ->
                            val id = entry.arguments?.getInt("id") ?: 0
                            QuizScreen(id = id, dao = db.cardsDao, db.statsDao, navController)
                        }
                        composable(route = "${Screen.TestQuizScreen.route}?id={id}" ,arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                                defaultValue = 0
                                nullable = false
                            }
                        )
                        ) { entry ->
                            val id = entry.arguments?.getInt("id") ?: 0
                            TrainingQuizScreen(id = id, dao = db.cardsDao, statsDao = db.statsDao)
                        }
                        composable(route = "${Screen.ExportImportScreen.route}?id={id}" ,arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                                defaultValue = 0
                                nullable = false
                            }
                        )
                        ) { entry ->
                            val id = entry.arguments?.getInt("id") ?: 0
                            ExportImportScreen(id = id, dao = db.cardsDao, decksDAO = db.decksDao)
                        }
                        composable(route = Screen.SettingsScreen.route)
                        {
                            SettingsScreen(db.cardsDao, db.decksDao, db.folderDao)
                        }
                        composable(route = Screen.statsScreen.route){
                            StatisticsScreen(statsDao = db.statsDao)
                        }
                    }
                }
            }
        }
    }
    private fun hasRequiredPermissions(): Boolean{
        return REQUIREDPERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    companion object {
        val REQUIREDPERMISSIONS  = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}


