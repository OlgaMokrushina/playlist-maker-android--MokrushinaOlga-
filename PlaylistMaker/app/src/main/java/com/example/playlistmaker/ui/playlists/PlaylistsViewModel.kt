package com.example.playlistmaker.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistsViewModel : ViewModel() {

    private val networkClient: NetworkClient = RetrofitNetworkClient()

    private val playlistsRepository: PlaylistsRepository =
        PlaylistsRepositoryImpl()

    private val tracksRepository: TracksRepository =
        TracksRepositoryImpl(
            networkClient = networkClient,
            scope = viewModelScope
        )

    val playlists: StateFlow<List<Playlist>> = playlistsRepository.getAllPlaylists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteList: StateFlow<List<Track>> = tracksRepository.getFavoriteTracks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun createNewPlayList(
        namePlaylist: String,
        description: String,
        coverImageUri: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.addNewPlaylist(
                name = namePlaylist,
                description = description,
                coverImageUri = coverImageUri
            )
        }
    }

    fun observeTrack(track: Track) {
        viewModelScope.launch {
            tracksRepository.getTrackByNameAndArtist(track).collect { existingTrack ->
                _isFavorite.value = existingTrack?.favorite ?: false
            }
        }
    }

    suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        tracksRepository.insertTrackToPlaylist(track, playlistId)
    }

    suspend fun isTrackAlreadyInPlaylist(trackId: Long, playlistId: Long): Boolean {
        return tracksRepository.isTrackInPlaylist(trackId, playlistId)
    }

    fun toggleFavorite(track: Track, isFavorite: Boolean) {
        viewModelScope.launch {
            tracksRepository.updateTrackFavoriteStatus(track, isFavorite)
            _isFavorite.value = isFavorite
        }
    }

    suspend fun deleteTrackFromPlaylist(track: Track) {
        tracksRepository.deleteTrackFromPlaylist(track)
    }

    suspend fun deletePlaylistById(id: Long) {
        playlistsRepository.deletePlaylistById(id)
    }

    suspend fun isExist(track: Track): Track? {
        return tracksRepository.getTrackByNameAndArtist(track).firstOrNull()
    }
}