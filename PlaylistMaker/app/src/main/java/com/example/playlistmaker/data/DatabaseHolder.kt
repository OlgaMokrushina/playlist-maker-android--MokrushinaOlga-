package com.example.playlistmaker.data

import kotlinx.coroutines.MainScope

object DatabaseHolder {
    val database: DatabaseMock by lazy {
        DatabaseMock(scope = MainScope())
    }
}