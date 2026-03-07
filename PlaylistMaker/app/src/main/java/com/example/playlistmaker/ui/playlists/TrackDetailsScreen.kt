package com.example.playlistmaker.ui.playlists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.launch

@Composable
fun PlaylistBottomSheetItem(
    playlistName: String,
    tracksCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = playlistName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Треков: $tracksCount",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsScreen(
    track: Track,
    playlistsViewModel: PlaylistsViewModel,
    onBack: () -> Unit
) {
    val playlists by playlistsViewModel.playlists.collectAsState(initial = emptyList())

    var isFavorite by remember { mutableStateOf(track.favorite) }
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Трек") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = track.trackName,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )

                Text(
                    text = "Длительность: ${track.trackTime}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Button(
                    onClick = {
                        isFavorite = !isFavorite
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Избранное",
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                    Text(
                        text = if (isFavorite) " Убрать из избранного" else " Добавить в избранное"
                    )
                }

                Button(
                    onClick = {
                        showBottomSheet = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить в плейлист"
                    )
                    Text(" Добавить в плейлист")
                }
            }

            LaunchedEffect(isFavorite) {
                playlistsViewModel.toggleFavorite(track, isFavorite)
            }

            if (showBottomSheet) {
                val sheetState = rememberModalBottomSheetState()

                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Выберите плейлист",
                            style = MaterialTheme.typography.titleLarge
                        )

                        if (playlists.isEmpty()) {
                            Text("Плейлистов пока нет")
                        } else {
                            playlists.forEach { playlist ->
                                PlaylistBottomSheetItem(
                                    playlistName = playlist.name,
                                    tracksCount = playlist.tracks.size,
                                    onClick = {
                                        kotlinx.coroutines.MainScope().launch {
                                            playlistsViewModel.insertTrackToPlaylist(
                                                track = track,
                                                playlistId = playlist.id
                                            )
                                        }
                                        showBottomSheet = false
                                    }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}