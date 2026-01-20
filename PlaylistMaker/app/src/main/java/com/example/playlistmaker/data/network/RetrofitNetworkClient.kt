package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.BaseResponse
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse

class RetrofitNetworkClient : NetworkClient {

    override fun doRequest(dto: Any): BaseResponse {
        return when (dto) {
            is TracksSearchRequest -> {
                val query = dto.expression.trim().lowercase()

                // простая фильтрация демо-списка по запросу
                val filtered = demoTracks().filter { track ->
                    track.trackName.lowercase().contains(query) ||
                            track.artistName.lowercase().contains(query)
                }

                TracksSearchResponse(filtered).apply { resultCode = 200 }
            }

            else -> BaseResponse().apply { resultCode = 400 }
        }
    }

    private fun demoTracks(): List<TrackDto> = listOf(
        TrackDto("Love Me Again", "John Newman", 245000),
        TrackDto("Crazy In Love", "Beyoncé", 236000),
        TrackDto("I Love Rock 'N Roll", "Joan Jett & the Blackhearts", 175000),
        TrackDto("Love Yourself", "Justin Bieber", 234000),
        TrackDto("Lose Yourself", "Eminem", 326000),
    )
}
