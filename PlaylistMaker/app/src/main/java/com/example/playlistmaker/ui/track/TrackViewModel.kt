package com.example.playlistmaker.ui.track.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TrackViewModel(
    private val trackId: String
) : ViewModel() {

    init {
        Log.d("TEST", "init!: $trackId")
    }

    companion object {
        fun getViewModelFactory(trackId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TrackViewModel(trackId) as T
                }
            }
    }
}
