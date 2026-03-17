package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.PlaylistWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylistById(id: Long)

    @Transaction
    @Query("SELECT * FROM playlists ORDER BY id DESC")
    fun getAllPlaylistsWithTracks(): Flow<List<PlaylistWithTracks>>

    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :playlistId LIMIT 1")
    fun getPlaylistWithTracksById(playlistId: Long): Flow<PlaylistWithTracks?>
}