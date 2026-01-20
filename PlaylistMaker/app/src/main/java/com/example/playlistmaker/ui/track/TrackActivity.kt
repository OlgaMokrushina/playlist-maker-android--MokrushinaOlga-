package com.example.playlistmaker.ui.track

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.playlistmaker.ui.track.viewmodel.TrackViewModel

class TrackActivity : ComponentActivity() {

    private val viewModel: TrackViewModel by viewModels {
        TrackViewModel.getViewModelFactory("123")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.hashCode()
    }
}
