package com.example.playlistmaker.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksRepository: TracksRepository
) : ViewModel() {

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState = _searchScreenState.asStateFlow()

    fun search(whatSearch: String) {
        Log.d("TEST", "search() called: '$whatSearch'")

        if (whatSearch.isBlank()) {
            _searchScreenState.update { SearchState.Initial }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _searchScreenState.update { SearchState.Searching }

                val list = tracksRepository.searchTracks(expression = whatSearch)

                _searchScreenState.update { SearchState.Success(foundList = list) }
            } catch (e: Exception) {
                Log.d("TEST", "ERROR: ${e::class.java.simpleName} ${e.message}", e)
                _searchScreenState.update { SearchState.Fail(e.message.toString()) }
            }
        }
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(
                        SearchCreator.provideTracksRepository()
                    ) as T
                }
            }
    }
}
