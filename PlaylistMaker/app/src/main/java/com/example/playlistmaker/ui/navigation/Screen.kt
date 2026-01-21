package com.example.playlistmaker.ui.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Search : Screen("search")
    object Settings : Screen("settings")
    object Playlists : Screen("playlists")
    object Favorites : Screen("favorites")
}

