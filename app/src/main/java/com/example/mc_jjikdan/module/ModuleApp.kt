package com.example.mc_jjikdan.module

import com.example.mc_jjikdan.api.diary.interfaces.IDiaryService
import com.example.mc_jjikdan.constants.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuleApp {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.apiURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideDiaryAPI(retrofit: Retrofit): IDiaryService = retrofit.create(IDiaryService::class.java)
}