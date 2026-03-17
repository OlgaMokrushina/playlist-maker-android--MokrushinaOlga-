package com.example.playlistmaker.ui.playlists

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.data.PlaylistsRepositoryImpl
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistViewModel(
    playlistId: Long
) : ViewModel() {

    private val playlistsRepository: PlaylistsRepository =
        PlaylistsRepositoryImpl()

    val playlist: Flow<Playlist?> = playlistsRepository.getPlaylist(playlistId)
}