package com.example.playlistmaker.ui.search

import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.TracksRepository
import kotlinx.coroutines.CoroutineScope

object SearchCreator {
    fun provideTracksRepository(scope: CoroutineScope): TracksRepository {
        return Creator.provideTracksRepository(scope)
    }
}