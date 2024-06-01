package com.example.mc_jjikdan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FoodRecord : ViewModel() {

    private val mealsLiveData = MutableLiveData<List<Meal>>()

    fun getMealsForDate(date: String): LiveData<List<Meal>> {
        // Sample 데이터 임시생성
        val sampleMeals = listOf(
            Meal("샐러드", date, "12:00 PM", "320", "https://example.com/salad.jpg"),
            Meal("단백질 바", date, "3:00 PM", "150", "https://example.com/protein_bar.jpg"),
            Meal("아메리카노", date, "5:00 PM", "60", "https://example.com/americano.jpg"),
            Meal("마라샹궈", date, "8:00 PM", "880", "https://example.com/hagimara.jpg")
        )
        mealsLiveData.value = sampleMeals
        return mealsLiveData
    }
}
