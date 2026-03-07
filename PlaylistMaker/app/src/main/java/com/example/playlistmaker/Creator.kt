package com.example.playlistmaker

import kotlinx.coroutines.CoroutineScope
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TracksRepository

object Creator {

    private fun provideNetworkClient(): NetworkClient {
        return RetrofitNetworkClient()
    }

    fun provideTracksRepository(scope: CoroutineScope): TracksRepository {
        return TracksRepositoryImpl(
            networkClient = provideNetworkClient(),
            scope = scope
        )
    }
}