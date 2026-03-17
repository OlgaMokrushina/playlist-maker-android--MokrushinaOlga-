package com.example.playlistmaker.domain.api

interface SearchHistoryRepository {

    fun addSearch(query: String)

    suspend fun getHistory(): List<String>

}