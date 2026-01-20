package com.example.playlistmaker.ui.search

import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.TracksRepository

object SearchCreator {
    fun provideTracksRepository(): TracksRepository {
        return Creator.provideTracksRepository()
    }
}
