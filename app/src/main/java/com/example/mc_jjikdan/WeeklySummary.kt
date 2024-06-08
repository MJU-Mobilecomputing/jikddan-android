package com.example.mc_jjikdan

data class WeeklySummary(
    val month: Int,
    val weekOfMonth: Int,
    val weeklyFoodMoisture: Int,
    val weeklySalt: Int,
    val weeklyCarbon: Int,
    val weeklyFat: Int,
    val weeklyProtein: Int,
    val weeklyAverageScore: Int
)