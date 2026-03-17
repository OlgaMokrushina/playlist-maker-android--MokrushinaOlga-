package com.example.playlistmaker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun search(query: String) {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isBlank()) {
            _searchScreenState.value = SearchState.Initial
            loadHistory()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _searchScreenState.value = SearchState.Searching

                val result = tracksRepository.searchTracks(trimmedQuery)

                searchHistoryRepository.addSearch(trimmedQuery)
                _history.emit(searchHistoryRepository.getHistory())

                _searchScreenState.value = SearchState.Success(result)
            } catch (e: Exception) {
                _searchScreenState.value = SearchState.Fail(
                    e.message ?: "Unknown error"
                )
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            _history.emit(searchHistoryRepository.getHistory())
        }
    }

    fun clearSearch() {
        _searchScreenState.value = SearchState.Initial
        loadHistory()
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