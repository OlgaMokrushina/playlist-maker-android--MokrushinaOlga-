package com.example.playlistmaker.ui.playlists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

@Composable
private fun TrackCover(
    artworkUrl100: String?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = artworkUrl100,
        contentDescription = null,
        modifier = modifier
            .size(45.dp)
            .clip(MaterialTheme.shapes.small),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.ic_music),
        error = painterResource(id = R.drawable.ic_music),
        fallback = painterResource(id = R.drawable.ic_music)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TrackListItem(
    track: Track,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TrackCover(
            artworkUrl100 = track.artworkUrl100
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.trackName,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${track.artistName} • ${track.trackTime}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    playlistsViewModel: PlaylistsViewModel,
    onBack: () -> Unit,
    onTrackClick: (Track) -> Unit
) {
    val favorites by playlistsViewModel.favoriteList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранные треки") },
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
        if (favorites.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = R.drawable.ic_music,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Ваша медиатека пуста",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(
                    items = favorites,
                    key = { it.id }
                ) { track ->
                    TrackListItem(
                        track = track,
                        onClick = { onTrackClick(track) },
                        onLongClick = {
                            playlistsViewModel.toggleFavorite(track, false)
                        }
                    )
                    HorizontalDivider(thickness = 0.5.dp)
                }
            }
        }
    }
}