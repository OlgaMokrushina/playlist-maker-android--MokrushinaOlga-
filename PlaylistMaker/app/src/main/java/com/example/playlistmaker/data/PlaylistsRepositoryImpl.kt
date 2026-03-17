package com.example.playlistmaker.data

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.PlaylistWithTracks
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl : PlaylistsRepository {

    private val playlistDao = DatabaseHolder.database.playlistDao()

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return playlistDao.getPlaylistWithTracksById(playlistId).map { playlistWithTracks ->
            playlistWithTracks?.toDomain()
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylistsWithTracks().map { playlists ->
            playlists.map { it.toDomain() }
        }
    }

    override suspend fun addNewPlaylist(
        name: String,
        description: String,
        coverImageUri: String?
    ) {
        playlistDao.insertPlaylist(
            PlaylistEntity(
                name = name,
                description = description,
                coverImageUri = coverImageUri
            )
        )
    }

    override suspend fun deletePlaylistById(id: Long) {
        playlistDao.deletePlaylistById(id)
    }

    private fun PlaylistWithTracks.toDomain(): Playlist {
        return Playlist(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverImageUri = playlist.coverImageUri,
            tracks = tracks.map { it.toDomain() }
        )
    }

    private fun TrackEntity.toDomain(): Track {
        return Track(
            id = id,
            trackName = trackName,
            artistName = artistName,
            trackTime = trackTime,
            artworkUrl100 = artworkUrl100,
            favorite = favorite,
            playlistId = 0
        )
    }
}