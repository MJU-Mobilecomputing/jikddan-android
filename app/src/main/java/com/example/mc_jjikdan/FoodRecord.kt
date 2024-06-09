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
    val solutionLiveData = MutableLiveData<String?>()

    private val apiService = RetrofitClient.apiService

    fun getMealsForDate(nickname: String, date: String): LiveData<List<Meal>> {
        apiService.getMealsForDate(nickname, date).enqueue(object : Callback<List<Meal>> {
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

    fun getDailySummary(nickname: String, date: String): LiveData<DailySummary?> {
        apiService.getDailySummary(nickname, date).enqueue(object : Callback<DailySummary> {
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

    fun getWeeklySummary(month: Int, weekNum: Int) {
        apiService.getWeeklySummary(month, weekNum).enqueue(object : Callback<WeeklySummaryResponse> {
            override fun onResponse(call: Call<WeeklySummaryResponse>, response: Response<WeeklySummaryResponse>) {
                if (response.isSuccessful) {
                    val weeklySummaryResponse = response.body()
                    weeklySummaryLiveData.value = weeklySummaryResponse?.weeklySummary ?: WeeklySummary(
                        month = month,
                        weekOfMonth = weekNum,
                        weeklyFoodMoisture = 0,
                        weeklySalt = 0,
                        weeklyCarbon = 0,
                        weeklyFat = 0,
                        weeklyProtein = 0,
                        weeklyAverageScore = 0
                    )
                    solutionLiveData.value = weeklySummaryResponse?.solution ?: "데이터가 없습니다."
                } else {
                    weeklySummaryLiveData.value = WeeklySummary(
                        month = month,
                        weekOfMonth = weekNum,
                        weeklyFoodMoisture = 0,
                        weeklySalt = 0,
                        weeklyCarbon = 0,
                        weeklyFat = 0,
                        weeklyProtein = 0,
                        weeklyAverageScore = 0
                    )
                    solutionLiveData.value = "데이터가 없습니다."
                }
            }

            override fun onFailure(call: Call<WeeklySummaryResponse>, t: Throwable) {
                Log.e("FoodRecordViewModel", "Error fetching weekly summary", t)
                weeklySummaryLiveData.value = WeeklySummary(
                    month = month,
                    weekOfMonth = weekNum,
                    weeklyFoodMoisture = 0,
                    weeklySalt = 0,
                    weeklyCarbon = 0,
                    weeklyFat = 0,
                    weeklyProtein = 0,
                    weeklyAverageScore = 0
                )
                solutionLiveData.value = "데이터가 없습니다."
            }
        })
    }


    fun updateMeal(nickname: String, meal: Meal) {
        apiService.updateMeal(nickname, meal.id, meal).enqueue(object : Callback<Meal> {
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

    fun deleteMeal(nickname: String, meal: Meal) {
        apiService.deleteMeal(nickname, meal.id).enqueue(object : Callback<Void> {
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
