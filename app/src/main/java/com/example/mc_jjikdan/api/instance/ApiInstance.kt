package com.example.mc_jjikdan.api.instance

import com.example.mc_jjikdan.constants.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiInstance {
    companion object {
        private const val BASE_URL = Constants.apiURL
        private val retrofitBuilder: Retrofit.Builder =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
    }

    private val retrofit = retrofitBuilder.build()
    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    fun <T> CreateService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}