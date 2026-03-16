package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.BaseResponse
import com.example.playlistmaker.data.dto.TracksSearchRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesApi = retrofit.create(ITunesApi::class.java)

    override fun doRequest(dto: Any): BaseResponse {
        if (dto !is TracksSearchRequest) {
            return BaseResponse().apply { resultCode = 400 }
        }

        return try {
            val response = itunesApi.searchTracks(
                expression = dto.expression
            ).execute()

            val body = response.body()

            if (response.isSuccessful && body != null) {
                body.apply { resultCode = 200 }
            } else {
                BaseResponse().apply { resultCode = response.code() }
            }
        } catch (e: Exception) {
            BaseResponse().apply { resultCode = 500 }
        }
    }
}