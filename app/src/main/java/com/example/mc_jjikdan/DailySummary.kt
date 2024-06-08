package com.example.mc_jjikdan

data class DailySummary(
    val date: String,
    val totalCalories: Int,
    val totalCarbs: Int,
    val totalProtein: Int,
    val totalFat: Int,
    val totalSalt: Int,
    val totalMoisture: Int
)
