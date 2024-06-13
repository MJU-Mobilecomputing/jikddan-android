package com.example.mc_jjikdan.api.diary.dto

data class Menu(
    val id: Int,
    val diary_day_id: Int,
    val date: String,
    val img: String,
    var summary: String,
    var total_cal: Int,
    var created_at: String,
    val updated_at: String,
    val food_moisture: Int,
    val salt: Int,
    val score: Int,
    val carbon: Int,
    val fat: Int,
    val protein: Int
)

