package com.example.playlistmaker.data

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DatabaseMock(
    private val scope: CoroutineScope
) {

    private val historyList = mutableListOf<String>()

    private val playlistsState = MutableStateFlow<List<Playlist>>(emptyList())
    private val tracksState = MutableStateFlow<List<Track>>(emptyList())

    fun getHistory(): List<String> {
        return historyList.toList()
    }

    fun addToHistory(word: String) {
        historyList.add(word)
    }

    fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistsState.map { playlists ->
            val tracks = tracksState.value
            playlists.map { playlist ->
                playlist.copy(
                    tracks = tracks.filter { it.playlistId == playlist.id }
                )
            }
        }
    }

    fun getPlaylist(id: Long): Flow<Playlist?> {
        return playlistsState.map { playlists ->
            val playlist = playlists.find { it.id == id }
            if (playlist != null) {
                playlist.copy(
                    tracks = tracksState.value.filter { it.playlistId == playlist.id }
                )
            } else {
                null
            }
        }
    }

    fun addNewPlaylist(name: String, description: String) {
        val current = playlistsState.value.toMutableList()
        current.add(
            Playlist(
                id = current.size.toLong() + 1,
                name = name,
                description = description,
                tracks = emptyList()
            )
        )
        playlistsState.value = current
    }

    fun deletePlaylistById(playlistId: Long) {
        playlistsState.value = playlistsState.value.filterNot { it.id == playlistId }
    }

    fun deleteTrackFromPlaylist(trackId: Long) {
        tracksState.value = tracksState.value.filterNot { it.id == trackId }
    }

    fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return tracksState.map { tracks ->
            tracks.find {
                it.trackName == track.trackName && it.artistName == track.artistName
            }
        }
    }

    fun insertTrack(track: Track) {
        val current = tracksState.value.toMutableList()
        current.removeIf { it.id == track.id }
        current.add(track)
        tracksState.value = current
    }

    fun getFavoriteTracks(): Flow<List<Track>> {
        return tracksState.map { tracks ->
            tracks.filter { it.favorite }
        }
    }

    fun deleteTracksByPlaylistId(playlistId: Long) {
        tracksState.value = tracksState.value.filterNot { it.playlistId == playlistId }
    }

    fun searchTracks(expression: String): List<Track> {
        return tracksState.value.filter {
            it.trackName.contains(expression, ignoreCase = true)
        }
    }
}