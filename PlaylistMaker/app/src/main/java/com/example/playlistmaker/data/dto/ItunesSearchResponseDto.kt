package com.example.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName

data class ItunesSearchResponseDto(
    @SerializedName("resultCount")
    val resultCount: Int,

    @SerializedName("results")
    val results: List<TrackDto>
)
