package com.example.mc_jjikdan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mc_jjikdan.api.diary.dto.Diary
import com.example.mc_jjikdan.api.diary.dto.Menu
import com.example.mc_jjikdan.api.diary.interfaces.IDiaryService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val api: IDiaryService
) : ViewModel() {
    private val _diary = MutableLiveData<Diary>()
    val diary: LiveData<Diary> get() = _diary

    fun getData(date: String) {
        viewModelScope.launch {
            try {
                _diary.value = api.getDiary(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateMeal(meal: Menu) {
        _diary.value?.diary_menus?.let { meals ->
            val updatedMeals = meals.toMutableList()
            val index = updatedMeals.indexOfFirst { it.id == meal.id }
            if (index != -1) {
                updatedMeals[index] = meal
                _diary.value = _diary.value?.copy(diary_menus = updatedMeals)
            }
        }
    }

    fun deleteMeal(meal: Menu) {
        _diary.value?.diary_menus?.let { meals ->
            val updatedMeals = meals.toMutableList()
            updatedMeals.removeAt(updatedMeals.indexOf(meal))
            _diary.value = _diary.value?.copy(diary_menus = updatedMeals)
        }
    }
}

