package com.example.playlistmaker.data

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class PlaylistsRepositoryImpl : PlaylistsRepository {

    private val playlistDao = DatabaseHolder.database.playlistDao()
    private val trackDao = DatabaseHolder.database.trackDao()

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return combine(
            playlistDao.getPlaylistById(playlistId),
            trackDao.getTracksByPlaylistId(playlistId)
        ) { playlistEntity, trackEntities ->
            playlistEntity?.toDomain(
                tracks = trackEntities.map { it.toDomain() }
            )
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return combine(
            playlistDao.getAllPlaylists(),
            trackDao.getAllTracks()
        ) { playlistEntities, trackEntities ->

            playlistEntities.map { playlistEntity ->
                val playlistTracks = trackEntities
                    .filter { it.playlistId == playlistEntity.id }
                    .map { it.toDomain() }

                playlistEntity.toDomain(playlistTracks)
            }
        }
    }

    override suspend fun addNewPlaylist(name: String, description: String) {
        playlistDao.insertPlaylist(
            PlaylistEntity(
                name = name,
                description = description
            )
        )
    }

    override suspend fun deletePlaylistById(id: Long) {
        playlistDao.deletePlaylistById(id)
    }

    private fun PlaylistEntity.toDomain(tracks: List<Track>): Playlist {
        return Playlist(
            id = id,
            name = name,
            description = description,
            tracks = tracks
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
            playlistId = playlistId
        )
    }
}