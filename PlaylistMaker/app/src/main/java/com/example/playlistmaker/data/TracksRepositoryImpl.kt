package com.example.playlistmaker.data

import com.example.playlistmaker.data.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val scope: CoroutineScope
) : TracksRepository {

    private val dao = DatabaseHolder.database.trackDao()

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        return if (response.resultCode == 200) {
            (response as TracksSearchResponse).results.map { dto ->
                val totalSeconds = dto.trackTimeMillis / 1000
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60

                Track(
                    id = dto.trackId,
                    trackName = dto.trackName,
                    artistName = dto.artistName,
                    trackTime = "%02d:%02d".format(minutes, seconds),
                    artworkUrl100 = dto.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"),
                    favorite = false,
                    playlistId = 0
                )
            }
        } else {
            throw RuntimeException("Server error")
        }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return dao.getTrackByNameAndArtist(
            trackName = track.trackName,
            artistName = track.artistName
        ).map { entity ->
            entity?.toDomain()
        }
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return dao.getFavoriteTracks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        val currentTrack = dao.getTrackById(track.id)

        val trackEntity = if (currentTrack != null) {
            currentTrack.copy(
                trackName = track.trackName,
                artistName = track.artistName,
                trackTime = track.trackTime,
                artworkUrl100 = track.artworkUrl100
            )
        } else {
            TrackEntity(
                id = track.id,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTime = track.trackTime,
                artworkUrl100 = track.artworkUrl100,
                favorite = track.favorite
            )
        }

        dao.insertTrack(trackEntity)

        dao.insertTrackToPlaylistCrossRef(
            PlaylistTrackCrossRef(
                playlistId = playlistId,
                trackId = track.id
            )
        )
    }

    override suspend fun isTrackInPlaylist(trackId: Long, playlistId: Long): Boolean {
        return dao.isTrackInPlaylist(trackId, playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        dao.deleteTrackRelations(track.id)
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        val currentTrack = dao.getTrackById(track.id)

        val updatedTrack = if (currentTrack != null) {
            currentTrack.copy(favorite = isFavorite)
        } else {
            TrackEntity(
                id = track.id,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTime = track.trackTime,
                artworkUrl100 = track.artworkUrl100,
                favorite = isFavorite
            )
        }

        dao.insertTrack(updatedTrack)
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