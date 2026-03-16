package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {

    @GET("search")
    fun searchTracks(
        @Query("term") expression: String,
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 20
    ): Call<TracksSearchResponse>
}