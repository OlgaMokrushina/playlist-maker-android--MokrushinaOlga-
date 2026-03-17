package com.example.playlistmaker.data

import ru.yandex.practicum.playlistmaker.data.preferences.SearchHistoryPreferences
import com.example.playlistmaker.domain.api.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val preferences: SearchHistoryPreferences
) : SearchHistoryRepository {

    override fun addSearch(query: String) {
        preferences.addEntry(query)
    }

    override suspend fun getHistory(): List<String> {
        return preferences.getEntries()
    }

}