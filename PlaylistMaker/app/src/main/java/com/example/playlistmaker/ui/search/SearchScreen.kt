package com.example.playlistmaker.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    onBack: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var showResults by remember { mutableStateOf(false) }

    val state by viewModel.searchScreenState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back)
                )
            }
            Text(
                text = stringResource(R.string.menu_search),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it }, // НЕ ищем автоматически
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = stringResource(R.string.search_placeholder)) },
            singleLine = true,
            leadingIcon = {
                IconButton(
                    onClick = {
                        viewModel.search(query)
                        showResults = query.isNotBlank()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.cd_search)
                    )
                }
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            query = ""
                            showResults = false
                            viewModel.search("") // вернём Initial
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.cd_clear)
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!showResults) {
            Text(text = stringResource(R.string.search_hint))
            return@Column
        }

        when (state) {
            is SearchState.Initial -> {
                Text(text = stringResource(R.string.search_hint))
            }

            is SearchState.Searching -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is SearchState.Success -> {
                val tracks = (state as SearchState.Success).foundList

                if (tracks.isEmpty()) {
                    Text(text = stringResource(R.string.search_nothing_found))
                } else {
                    LazyColumn {
                        items(tracks) { track ->
                            TrackItem(track = track)
                            HorizontalDivider(thickness = 0.5.dp)
                        }
                    }
                }
            }

            is SearchState.Fail -> {
                val error = (state as SearchState.Fail).error
                Text(text = stringResource(R.string.search_error, error))
            }
        }
    }
}

@Composable
private fun TrackItem(track: Track) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {  }
            .padding(vertical = 12.dp)
    ) {
        Text(text = track.trackName)
        Text(text = track.artistName)
        Text(text = track.trackTime)
    }
}
