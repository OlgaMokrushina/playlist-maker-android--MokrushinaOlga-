package com.example.playlistmaker.data

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val scope: CoroutineScope
) : TracksRepository {

    private val dao = DatabaseHolder.database.trackDao()

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        return if (response.resultCode == 200) {
            (response as TracksSearchResponse).results.map { dto ->
                val seconds = dto.trackTimeMillis / 1000
                val minutes = seconds / 60
                val trackTime = "%02d:%02d".format(minutes, seconds - minutes * 60)

                Track(
                    id = dto.trackId,
                    trackName = dto.trackName,
                    artistName = dto.artistName,
                    trackTime = trackTime,
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
        return dao.getFavoriteTracks().map { entityList ->
            entityList.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        val current = dao.getTrackById(track.id)

        val updatedTrack = track.copy(
            favorite = current?.favorite ?: track.favorite,
            playlistId = playlistId
        )

        dao.insertTrack(updatedTrack.toEntity())
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        val current = dao.getTrackById(track.id)

        val updatedTrack = track.copy(
            favorite = current?.favorite ?: track.favorite,
            playlistId = 0
        )

        dao.insertTrack(updatedTrack.toEntity())
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        val current = dao.getTrackById(track.id)

        val updatedTrack = track.copy(
            favorite = isFavorite,
            playlistId = current?.playlistId ?: track.playlistId
        )

        dao.insertTrack(updatedTrack.toEntity())
    }

    override fun deleteTracksByPlaylistId(playlistId: Long) {
        scope.launch {
            dao.deleteTracksByPlaylistId(playlistId)
        }
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

    private fun Track.toEntity(): TrackEntity {
        return TrackEntity(
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