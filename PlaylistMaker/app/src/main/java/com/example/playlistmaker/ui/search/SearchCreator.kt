package com.example.playlistmaker.ui.search

import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksRepository

object SearchCreator {

    fun provideTracksRepository(): TracksRepository {
        return Creator.provideTracksRepository()
    }

    fun provideSearchHistoryRepository(): SearchHistoryRepository {
        return Creator.provideSearchHistoryRepository()
    }
}