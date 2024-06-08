package com.example.mc_jjikdan.api.diary.dto

data class Diary(
    val diary_day_id: Int,
    val diary_date: String,
    val diary_menus: List<Menu>
)
