package com.example.playlistmaker.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.playlistmaker.ui.search.SearchScreen
import com.example.playlistmaker.ui.search.SearchViewModel
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

class MainActivity : ComponentActivity() {

    private val searchViewModel: SearchViewModel by viewModels {
        SearchViewModel.getViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PlaylistMakerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    SearchScreen(
                        modifier = Modifier.padding(paddingValues),
                        viewModel = searchViewModel
                    )
                }
            }
        }
    }
}
