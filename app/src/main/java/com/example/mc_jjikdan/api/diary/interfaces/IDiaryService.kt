package com.example.mc_jjikdan.api.diary.interfaces

import com.example.mc_jjikdan.api.diary.dto.Diary
import com.example.mc_jjikdan.constants.Constants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface IDiaryService {
    @GET("/api/v1/diary/{date}")
    suspend fun getDiary(@Query("nickname") nickname: String, @Path("date") date: String): Diary
}
