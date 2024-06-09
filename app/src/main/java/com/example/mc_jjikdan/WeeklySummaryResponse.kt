package com.example.mc_jjikdan

import com.google.gson.annotations.SerializedName

data class WeeklySummaryResponse(
    @SerializedName("weekly_summary")
    val weeklySummary: WeeklySummary,
    @SerializedName("solution")
    val solution: String
)