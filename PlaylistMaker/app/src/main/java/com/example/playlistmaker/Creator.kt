package com.example.playlistmaker

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TracksRepository

object Creator {

    private fun provideNetworkClient(): NetworkClient {
        return RetrofitNetworkClient()
    }

    fun provideTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(provideNetworkClient())
    }
}
