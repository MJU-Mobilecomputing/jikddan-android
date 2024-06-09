package com.example.mc_jjikdan

import com.google.gson.annotations.SerializedName

data class WeeklySummary(
    @SerializedName("month")
    val month: Int,
    @SerializedName("week_of_month")
    val weekOfMonth: Int,
    @SerializedName("weekly_food_moisture")
    val weeklyFoodMoisture: Int,
    @SerializedName("weekly_salt")
    val weeklySalt: Int,
    @SerializedName("weekly_carbon")
    val weeklyCarbon: Int,
    @SerializedName("weekly_fat")
    val weeklyFat: Int,
    @SerializedName("weekly_protein")
    val weeklyProtein: Int,
    @SerializedName("weekly_average_score")
    val weeklyAverageScore: Int
)