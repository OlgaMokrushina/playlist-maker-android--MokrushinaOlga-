package com.example.playlistmaker.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksRepository: TracksRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState = _searchScreenState.asStateFlow()

    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history = _history.asStateFlow()

    var selectedTrack: Track? = null
        private set

    fun selectTrack(track: Track) {
        selectedTrack = track
    }

    fun search(whatSearch: String) {
        Log.d("TEST", "search() called: '$whatSearch'")

        if (whatSearch.isBlank()) {
            _searchScreenState.update { SearchState.Initial }
            loadHistory()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _searchScreenState.update { SearchState.Searching }

                val list = tracksRepository.searchTracks(expression = whatSearch)

                searchHistoryRepository.addSearch(whatSearch)
                loadHistory()

                _searchScreenState.update {
                    SearchState.Success(foundList = list)
                }

            } catch (e: Exception) {
                Log.d("TEST", "ERROR: ${e::class.java.simpleName} ${e.message}", e)

                _searchScreenState.update {
                    SearchState.Fail(e.message.toString())
                }
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val historyList = searchHistoryRepository.getHistory()
            _history.emit(historyList)
        }
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(
                        SearchCreator.provideTracksRepository(),
                        SearchCreator.provideSearchHistoryRepository()
                    ) as T
                }
            }
    }
}