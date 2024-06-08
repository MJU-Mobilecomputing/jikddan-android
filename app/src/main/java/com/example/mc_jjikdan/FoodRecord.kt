package com.example.mc_jjikdan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodRecord : ViewModel() {
    val mealsLiveData = MutableLiveData<List<Meal>>()
    val dailySummaryLiveData = MutableLiveData<DailySummary?>()
    val weeklySummaryLiveData = MutableLiveData<WeeklySummary?>()

    private val apiService = RetrofitClient.apiService

    fun getMealsForDate(date: String): LiveData<List<Meal>> {
        apiService.getMealsForDate(date).enqueue(object : Callback<List<Meal>> {
            override fun onResponse(call: Call<List<Meal>>, response: Response<List<Meal>>) {
                if (response.isSuccessful) {
                    mealsLiveData.value = response.body()
                } else {
                    mealsLiveData.value = emptyList()
                }
            }

            override fun onFailure(call: Call<List<Meal>>, t: Throwable) {
                Log.e("FoodRecordViewModel", "Error fetching meals for date", t)
                mealsLiveData.value = emptyList()
            }
        })
        return mealsLiveData
    }

    fun getDailySummary(date: String): LiveData<DailySummary?> {
        apiService.getDailySummary(date).enqueue(object : Callback<DailySummary> {
            override fun onResponse(call: Call<DailySummary>, response: Response<DailySummary>) {
                if (response.isSuccessful) {
                    dailySummaryLiveData.value = response.body()
                } else {
                    dailySummaryLiveData.value = null
                }
            }

            override fun onFailure(call: Call<DailySummary>, t: Throwable) {
                Log.e("FoodRecordViewModel", "Error fetching daily summary", t)
                dailySummaryLiveData.value = null
            }
        })
        return dailySummaryLiveData
    }

    fun getWeeklySummary(month: Int, weekNum: Int): LiveData<WeeklySummary?> {
        apiService.getWeeklySummary(month, weekNum).enqueue(object : Callback<WeeklySummary> {
            override fun onResponse(call: Call<WeeklySummary>, response: Response<WeeklySummary>) {
                if (response.isSuccessful) {
                    weeklySummaryLiveData.value = response.body()
                } else {
                    weeklySummaryLiveData.value = null
                }
            }

            override fun onFailure(call: Call<WeeklySummary>, t: Throwable) {
                Log.e("FoodRecordViewModel", "Error fetching weekly summary", t)
                weeklySummaryLiveData.value = null
            }
        })
        return weeklySummaryLiveData
    }

    fun updateMeal(meal: Meal) {
        apiService.updateMeal(meal.id, meal).enqueue(object : Callback<Meal> {
            override fun onResponse(call: Call<Meal>, response: Response<Meal>) {
                if (response.isSuccessful) {
                    val updatedMeal = response.body()
                    updatedMeal?.let {
                        val currentMeals = mealsLiveData.value?.toMutableList()
                        currentMeals?.let { meals ->
                            val index = meals.indexOfFirst { it.id == meal.id }
                            if (index != -1) {
                                meals[index] = it
                                mealsLiveData.postValue(meals)
                            }
                        }
                    }
                } else {
                    Log.e("FoodRecordViewModel", "Error updating meal: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Meal>, t: Throwable) {
                Log.e("FoodRecordViewModel", "Error updating meal", t)
            }
        })
    }

    fun deleteMeal(meal: Meal) {
        apiService.deleteMeal(meal.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val currentMeals = mealsLiveData.value?.toMutableList()
                    currentMeals?.let {
                        it.remove(meal)
                        mealsLiveData.postValue(it)
                    }
                } else {
                    Log.e("FoodRecordViewModel", "Error deleting meal: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("FoodRecordViewModel", "Error deleting meal", t)
            }
        })
    }
}
