package com.example.playlistmaker.ui.playlists
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsScreen(
    track: Track,
    viewModel: PlaylistsViewModel,
    onBack: () -> Unit
) {
    val playlists by viewModel.playlists.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(track.id) {
        viewModel.observeTrack(track)
    }

    var showBottomSheet = remember { androidx.compose.runtime.mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = track.trackName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!track.artworkUrl100.isNullOrBlank()) {
                    AsyncImage(
                        model = track.artworkUrl100,
                        contentDescription = track.trackName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop,
                        error = androidx.compose.ui.res.painterResource(id = R.drawable.ic_music),
                        placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.ic_music)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_music),
                            contentDescription = track.trackName,
                            modifier = Modifier.size(96.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Длительность: ${track.trackTime}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            viewModel.toggleFavorite(track, !isFavorite)
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Избранное",
                            tint = if (isFavorite) Color(0xFFFF3B30) else MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            showBottomSheet.value = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlaylistAdd,
                            contentDescription = "Добавить в плейлист",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            if (showBottomSheet.value) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet.value = false
                    },
                    sheetState = sheetState
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Добавить в плейлист",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (playlists.isEmpty()) {
                            Text(
                                text = "Плейлистов пока нет",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        } else {
                            LazyColumn {
                                items(playlists) { playlist ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                coroutineScope.launch {
                                                    val alreadyExists = viewModel.isTrackAlreadyInPlaylist(
                                                        trackId = track.id,
                                                        playlistId = playlist.id
                                                    )

                                                    if (alreadyExists) {
                                                        snackbarHostState.showSnackbar(
                                                            "Трек уже добавлен в плейлист ${playlist.name}"
                                                        )
                                                    } else {
                                                        viewModel.insertTrackToPlaylist(
                                                            track = track,
                                                            playlistId = playlist.id
                                                        )
                                                        snackbarHostState.showSnackbar(
                                                            "Добавлено в плейлист ${playlist.name}"
                                                        )
                                                        showBottomSheet.value = false
                                                    }
                                                }
                                            }
                                            .padding(vertical = 12.dp)
                                    ) {
                                        if (!playlist.coverImageUri.isNullOrBlank()) {
                                            AsyncImage(
                                                model = playlist.coverImageUri,
                                                contentDescription = playlist.name,
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(RoundedCornerShape(8.dp)),
                                                contentScale = ContentScale.Crop,
                                                error = androidx.compose.ui.res.painterResource(id = R.drawable.ic_music),
                                                placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.ic_music)
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Image(
                                                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_music),
                                                    contentDescription = playlist.name,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = playlist.name,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Medium
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            val tracksCount = playlist.tracks.size
                                            Text(
                                                text = when {
                                                    tracksCount % 10 == 1 && tracksCount % 100 != 11 -> "$tracksCount трек"
                                                    tracksCount % 10 in 2..4 && tracksCount % 100 !in 12..14 -> "$tracksCount трека"
                                                    else -> "$tracksCount треков"
                                                },
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}