package com.example.mc_jjikdan.api.diary.interfaces

import com.example.mc_jjikdan.api.diary.dto.Diary
import retrofit2.http.GET
import retrofit2.http.Path


interface IDiaryService {
    @GET("/api/v1/diary/{date}")
    suspend fun getDiary(@Path("date") date: String): Diary
}
