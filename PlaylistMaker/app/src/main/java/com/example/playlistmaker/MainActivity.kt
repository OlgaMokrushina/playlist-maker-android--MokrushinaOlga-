package com.example.playlistmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.playlistmaker.ui.main.MainScreen
import com.example.playlistmaker.ui.navigation.Screen
import com.example.playlistmaker.ui.playlists.FavoritesScreen
import com.example.playlistmaker.ui.playlists.NewPlaylistScreen
import com.example.playlistmaker.ui.playlists.PlaylistsScreen
import com.example.playlistmaker.ui.playlists.PlaylistsViewModel
import com.example.playlistmaker.ui.playlists.TrackDetailsScreen
import com.example.playlistmaker.ui.search.SearchScreen
import com.example.playlistmaker.ui.search.SearchViewModel
import com.example.playlistmaker.ui.settings.SettingsScreen
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

class MainActivity : ComponentActivity() {

    private val searchViewModel: SearchViewModel by viewModels {
        SearchViewModel.getViewModelFactory()
    }

    private val playlistsViewModel: PlaylistsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PlaylistMakerTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Main.route,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(Screen.Main.route) {
                            MainScreen(
                                onSearchClick = { navController.navigate(Screen.Search.route) },
                                onPlaylistsClick = { navController.navigate(Screen.Playlists.route) },
                                onFavoritesClick = { navController.navigate(Screen.Favorites.route) },
                                onSettingsClick = { navController.navigate(Screen.Settings.route) }
                            )
                        }

                        composable(Screen.Search.route) {
                            SearchScreen(
                                modifier = Modifier.fillMaxSize(),
                                viewModel = searchViewModel,
                                onBack = { navController.popBackStack() },
                                onTrackClick = { track ->
                                    searchViewModel.selectTrack(track)
                                    navController.navigate(Screen.TrackDetails.route)
                                }
                            )
                        }

                        composable(Screen.TrackDetails.route) {
                            val selectedTrack = searchViewModel.selectedTrack.value
                            if (selectedTrack != null) {
                                TrackDetailsScreen(
                                    track = selectedTrack,
                                    playlistsViewModel = playlistsViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }

                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                modifier = Modifier.fillMaxSize(),
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Playlists.route) {
                            PlaylistsScreen(
                                playlistsViewModel = playlistsViewModel,
                                addNewPlaylist = {
                                    navController.navigate(Screen.NewPlaylist.route)
                                },
                                navigateToPlaylist = { },
                                navigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.NewPlaylist.route) {
                            NewPlaylistScreen(
                                playlistsViewModel = playlistsViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Favorites.route) {
                            FavoritesScreen(
                                playlistsViewModel = playlistsViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}