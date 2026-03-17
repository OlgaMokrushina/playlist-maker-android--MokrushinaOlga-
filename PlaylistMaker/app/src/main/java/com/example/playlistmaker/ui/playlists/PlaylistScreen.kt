package com.example.playlistmaker.ui.playlists

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.io.File

@Composable
private fun PlaylistTrackItem(track: Track) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = track.trackName,
            placeholder = painterResource(id = R.drawable.ic_music),
            error = painterResource(id = R.drawable.ic_music),
            modifier = Modifier
                .size(56.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

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
                text = track.artistName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = track.trackTime,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

private fun buildShareText(
    playlistName: String,
    description: String,
    tracks: List<Track>
): String {
    val builder = StringBuilder()

    builder.appendLine(playlistName)

    if (description.isNotBlank()) {
        builder.appendLine(description)
    }

    builder.appendLine(
        when {
            tracks.size % 10 == 1 && tracks.size % 100 != 11 -> "${tracks.size} трек"
            tracks.size % 10 in 2..4 && tracks.size % 100 !in 12..14 -> "${tracks.size} трека"
            else -> "${tracks.size} треков"
        }
    )
    builder.appendLine()

    tracks.forEachIndexed { index, track ->
        builder.appendLine("${index + 1}. ${track.artistName} — ${track.trackName} (${track.trackTime})")
    }

    return builder.toString().trim()
}

private fun tracksCountText(count: Int): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> "$count трек"
        count % 10 in 2..4 && count % 100 !in 12..14 -> "$count трека"
        else -> "$count треков"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    playlistViewModel: PlaylistViewModel,
    onBack: () -> Unit,
    onDeletePlaylist: (Long) -> Unit
) {
    val playlist by playlistViewModel.playlist.collectAsState(initial = null)
    val context = LocalContext.current

    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()

    if (showDeleteDialog && playlist != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            text = {
                Text("Хотите удалить плейлист «${playlist!!.name}»?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeletePlaylist(playlist!!.id)
                    }
                ) {
                    Text("ДА")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("НЕТ")
                }
            }
        )
    }

    if (showBottomSheet && playlist != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(100.dp)
                        )
                        .width(32.dp)
                        .height(4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!playlist!!.coverImageUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = File(playlist!!.coverImageUri!!),
                            contentDescription = "Обложка плейлиста",
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.LightGray, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.LightGray, RoundedCornerShape(8.dp))
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = playlist!!.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = tracksCountText(playlist!!.tracks.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Поделиться",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val shareText = buildShareText(
                                playlistName = playlist!!.name,
                                description = playlist!!.description,
                                tracks = playlist!!.tracks
                            )

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }

                            context.startActivity(
                                Intent.createChooser(shareIntent, "Поделиться плейлистом")
                            )
                            showBottomSheet = false
                        }
                        .padding(vertical = 16.dp)
                )

                Text(
                    text = "Редактировать информацию",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showBottomSheet = false
                        }
                        .padding(vertical = 16.dp)
                )

                Text(
                    text = "Удалить плейлист",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showBottomSheet = false
                            showDeleteDialog = true
                        }
                        .padding(vertical = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(playlist?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    if (playlist != null) {
                        IconButton(
                            onClick = { showBottomSheet = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Меню плейлиста"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (playlist == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Плейлист не найден")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                if (!playlist!!.coverImageUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = File(playlist!!.coverImageUri!!),
                        contentDescription = "Обложка плейлиста",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(Color.LightGray)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = playlist!!.name,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (playlist!!.description.isNotBlank()) {
                    Text(
                        text = playlist!!.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = tracksCountText(playlist!!.tracks.size),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (playlist!!.tracks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("В этом плейлисте пока нет треков")
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
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