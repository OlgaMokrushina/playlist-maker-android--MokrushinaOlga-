package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object Creator {

    lateinit var application: Application

    private fun provideNetworkClient(): NetworkClient {
        return RetrofitNetworkClient()
    }

    fun provideTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(
            networkClient = provideNetworkClient(),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        )
    }

    fun provideSearchHistoryRepository(): SearchHistoryRepository {
        val app = application as MyApplication
        return SearchHistoryRepositoryImpl(app.searchHistoryPreferences)
    }
}