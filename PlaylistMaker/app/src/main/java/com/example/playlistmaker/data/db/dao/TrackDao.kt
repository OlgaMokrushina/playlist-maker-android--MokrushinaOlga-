package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistTrackCrossRef
import com.example.playlistmaker.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query(
        """
        SELECT * FROM tracks
        WHERE trackName = :trackName AND artistName = :artistName
        LIMIT 1
        """
    )
    fun getTrackByNameAndArtist(trackName: String, artistName: String): Flow<TrackEntity?>

    @Query(
        """
        SELECT * FROM tracks
        WHERE favorite = 1
        ORDER BY id DESC
        """
    )
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query(
        """
        SELECT * FROM tracks
        WHERE id = :trackId
        LIMIT 1
        """
    )
    suspend fun getTrackById(trackId: Long): TrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackToPlaylistCrossRef(crossRef: PlaylistTrackCrossRef)

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM playlist_track_cross_ref
            WHERE playlistId = :playlistId AND trackId = :trackId
        )
        """
    )
    suspend fun isTrackInPlaylist(trackId: Long, playlistId: Long): Boolean

    @Query(
        """
        DELETE FROM playlist_track_cross_ref
        WHERE playlistId = :playlistId
        """
    )
    suspend fun deletePlaylistRelations(playlistId: Long)

    @Query(
        """
        DELETE FROM playlist_track_cross_ref
        WHERE trackId = :trackId
        """
    )
    suspend fun deleteTrackRelations(trackId: Long)

    @Query(
        """
        DELETE FROM playlist_track_cross_ref
        WHERE playlistId = :playlistId AND trackId = :trackId
        """
    )
    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long)
}