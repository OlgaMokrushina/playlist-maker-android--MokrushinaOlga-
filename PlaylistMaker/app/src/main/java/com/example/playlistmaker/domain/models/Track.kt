package com.example.playlistmaker.domain.models

data class Track(
    val id: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String?,
    val favorite: Boolean = false,
    val playlistId: Long = 0
)