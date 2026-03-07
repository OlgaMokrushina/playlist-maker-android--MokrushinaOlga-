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
        TrackDto(
            trackId = 1L,
            trackName = "Love Me Again",
            artistName = "John Newman",
            trackTimeMillis = 245000
        ),
        TrackDto(
            trackId = 2L,
            trackName = "Crazy In Love",
            artistName = "Beyoncé",
            trackTimeMillis = 236000
        ),
        TrackDto(
            trackId = 3L,
            trackName = "I Love Rock 'N Roll",
            artistName = "Joan Jett & the Blackhearts",
            trackTimeMillis = 175000
        ),
        TrackDto(
            trackId = 4L,
            trackName = "Love Yourself",
            artistName = "Justin Bieber",
            trackTimeMillis = 234000
        ),
        TrackDto(
            trackId = 5L,
            trackName = "Lose Yourself",
            artistName = "Eminem",
            trackTimeMillis = 326000
        )
    )
}