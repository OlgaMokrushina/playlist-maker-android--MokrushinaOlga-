package com.example.playlistmaker.data

import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase

object DatabaseHolder {

    lateinit var database: AppDatabase

    fun init(context: Context) {
        database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "playlist_database"
        ).build()
    }
}