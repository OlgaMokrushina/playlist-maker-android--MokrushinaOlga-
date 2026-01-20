package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackSearchInteractor {
    fun searchTracks(expression: String): List<Track>
}
