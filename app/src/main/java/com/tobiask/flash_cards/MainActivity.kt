package com.tobiask.flash_cards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.tobiask.flash_cards.database.FlashCardsDatabase
import com.tobiask.flash_cards.navigation.Screen
import com.tobiask.flash_cards.screens.deck_screen_menu.DeckScreenMenu
import com.tobiask.flash_cards.screens.main_screen.MainScreen
import com.tobiask.flash_cards.screens.quiz_screen.QuizScreen
import com.tobiask.flash_cards.ui.theme.Flash_cardsTheme

class MainActivity : ComponentActivity() {
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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Screen.MainScreen.route){
                        composable(route = Screen.MainScreen.route) {
                            MainScreen(navController, db.decksDao, db.configDao, db.cardsDao)
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
                            DeckScreenMenu(dao = db.decksDao, daoCard = db.cardsDao,id = id, navController)
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
                            QuizScreen(id = id, dao = db.cardsDao, navController = navController)
                        }
                    }
                }
            }
        }
    }
}
