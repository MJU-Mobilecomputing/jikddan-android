package com.example.mc_jjikdan

import com.example.mc_jjikdan.api.diary.dto.Menu
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
interface ApiService {

    @GET("/api/v1/diary/{date}")
    fun getMealsForDate(@Query("nickname") nickname: String, @Path("date") date: String): Call<List<Meal>>

    @GET("/api/v1/diary/{date}/summary")
    fun getDailySummary(@Query("nickname") nickname: String, @Path("date") date: String): Call<DailySummary>

    @GET("/api/v1/weekly")
    fun getWeeklySummary(
        //@Query("nickname") nickname: String,
        @Query("month") month: Int,
        @Query("week_num") weekNum: Int
    ): Call<WeeklySummaryResponse>

    @Multipart
    @POST("/api/v1/menu")
    fun uploadMeal(
        @Query("nickname") nickname: String,
        @Part img: MultipartBody.Part,
        @Part("date") date: RequestBody,
        @Part("menu_time") menuTime: RequestBody
    ): Call<Menu>

    @PUT("/api/v1/menu/{id}")
    fun updateMeal(@Query("nickname") nickname: String, @Path("id") id: Int, @Body meal: Meal): Call<Meal>

    @DELETE("/api/v1/menu/{id}")
    fun deleteMeal(@Query("nickname") nickname: String, @Path("id") id: Int): Call<Void>
}
