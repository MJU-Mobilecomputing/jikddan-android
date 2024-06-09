package com.example.mc_jjikdan

data class MealResponse( // MealResponse는 서버에서 반환되는 JSON 데이터를 매핑하는 데이터 클래스
    val foodMoisture: Int,
    val salt: Int,
    val carbon: Int,
    val fat: Int,
    val protein: Int,
    val totalCal: Int
)
