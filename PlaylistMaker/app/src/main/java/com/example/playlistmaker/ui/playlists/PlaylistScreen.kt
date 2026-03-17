package com.example.playlistmaker.ui.playlists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.playlistmaker.domain.models.Track
import java.io.File

@Composable
private fun PlaylistTrackItem(track: Track) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = track.trackName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = track.artistName,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = track.trackTime,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    playlistViewModel: PlaylistViewModel,
    onBack: () -> Unit
) {
    val playlist by playlistViewModel.playlist.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(playlist?.name ?: "Плейлист") },
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
        if (playlist == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text("Плейлист не найден")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                if (!playlist!!.coverImageUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = File(playlist!!.coverImageUri!!),
                        contentDescription = "Обложка плейлиста",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(bottom = 16.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(Color.LightGray)
                            .padding(bottom = 16.dp)
                    ) {}
                }

                if (playlist!!.description.isNotBlank()) {
                    Text(
                        text = playlist!!.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Треков: ${playlist!!.tracks.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )
                } else {
                    Text(
                        text = "Треков: ${playlist!!.tracks.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (playlist!!.tracks.isEmpty()) {
                    Text("В этом плейлисте пока нет треков")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(playlist!!.tracks) { track ->
                            PlaylistTrackItem(track)
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}