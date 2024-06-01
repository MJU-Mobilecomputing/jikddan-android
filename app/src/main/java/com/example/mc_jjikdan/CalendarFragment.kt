package com.example.mc_jjikdan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var mealContainer: LinearLayout
    private lateinit var foodRecordFragment: FoodRecord  // FoodRecord

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        calendarView = view.findViewById(R.id.calendarView)
        mealContainer = view.findViewById(R.id.mealContainer)

        foodRecordFragment = ViewModelProvider(requireActivity()).get(FoodRecord::class.java)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth"
            loadMealsForDate(selectedDate)
        }

        return view
    }

    private fun loadMealsForDate(date: String) {
        foodRecordFragment.getMealsForDate(date).observe(viewLifecycleOwner, { meals ->
            if (mealContainer.childCount > 1) {
                mealContainer.removeViews(1, mealContainer.childCount - 1)
            }

            meals?.forEach { meal ->
                val mealView = layoutInflater.inflate(R.layout.meal_item, mealContainer, false)
                val mealName: TextView = mealView.findViewById(R.id.mealName)
                val mealDate: TextView = mealView.findViewById(R.id.mealDate)
                val mealTime: TextView = mealView.findViewById(R.id.mealTime)
                val mealCalories: TextView = mealView.findViewById(R.id.mealCalories)
                val mealImage: ImageView = mealView.findViewById(R.id.mealImage)

                mealName.text = meal.name
                mealDate.text = meal.date
                mealTime.text = meal.time
                mealCalories.text = "${meal.calories} Kcal"
                // meal.image를 URL 또는 리소스 ID로 가정
                //Glide.with(this).load(meal.image).into(mealImage)

                mealContainer.addView(mealView)
            }
        })
    }
}
