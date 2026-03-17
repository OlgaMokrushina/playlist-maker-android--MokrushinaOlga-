package com.example.playlistmaker

import android.app.Application
import androidx.datastore.preferences.preferencesDataStore
import com.example.playlistmaker.data.DatabaseHolder
import ru.yandex.practicum.playlistmaker.data.preferences.SearchHistoryPreferences

private val Application.dataStore by preferencesDataStore(
    name = "search_history"
)

class MyApplication : Application() {

    val searchHistoryPreferences by lazy {
        SearchHistoryPreferences(dataStore)
    }

    override fun onCreate() {
        super.onCreate()
        Creator.application = this
        DatabaseHolder.init(this)
    }
}