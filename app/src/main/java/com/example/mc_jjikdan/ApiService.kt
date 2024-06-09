package com.example.mc_jjikdan

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("/api/v1/diary/{date}")
    fun getMealsForDate(@Path("date") date: String): Call<List<Meal>>

    @GET("/api/v1/diary/{date}/summary")
    fun getDailySummary(@Path("date") date: String): Call<DailySummary>

    @GET("/api/v1/weekly")
    fun getWeeklySummary(@Query("month") month: Int, @Query("week_num") weekNum: Int): Call<WeeklySummary>

    @POST("/api/v1/menu")
    @FormUrlEncoded
    fun createMeal(
        @Field("img") img: String,
        @Field("date") date: String,
        @Field("menu_time") menuTime: String
    ): Call<Meal>

    fun getWeeklySummary(
        @Query("month") month: Int,
        @Query("week_num") weekNum: Int
    ): Call<WeeklySummaryResponse>


    @Multipart
    @POST("/api/v1/menu")
    fun uploadMeal(
        @Part img: MultipartBody.Part,
        @Part("date") date: RequestBody,
        @Part("menu_time") menuTime: RequestBody
    ): Call<MealResponse>

    @PUT("/api/v1/menu/{id}")
    fun updateMeal(@Path("id") id: Int, @Body meal: Meal): Call<Meal>

    @DELETE("/api/v1/menu/{id}")
    fun deleteMeal(@Path("id") id: Int): Call<Void>
}
