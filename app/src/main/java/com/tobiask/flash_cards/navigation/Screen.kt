package com.tobiask.flash_cards.navigation

sealed class Screen(val route: String){
    object MainScreen: Screen("main_screen")
    object DeckScreen: Screen("deck_screen")
    object QuizScreen: Screen("quiz_screen")
    object FolderScreen: Screen("folder_screen")
}
