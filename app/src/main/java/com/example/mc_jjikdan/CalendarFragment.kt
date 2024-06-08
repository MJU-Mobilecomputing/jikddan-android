package com.example.mc_jjikdan

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var mealContainer: LinearLayout
    private lateinit var foodRecordViewModel: FoodRecord // ViewModel
    private lateinit var noMealsMessage: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        calendarView = view.findViewById(R.id.calendarView)
        mealContainer = view.findViewById(R.id.mealContainer)
        noMealsMessage = view.findViewById(R.id.noMealsMessage)

        foodRecordViewModel = ViewModelProvider(requireActivity()).get(FoodRecord::class.java)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth"
            loadMealsForDate(selectedDate)
        }
        return view
    }

    private fun loadMealsForDate(date: String) {
        foodRecordViewModel.getMealsForDate(date).observe(viewLifecycleOwner, { meals ->
            mealContainer.removeViews(1, mealContainer.childCount - 1)

            if (meals.isNullOrEmpty()) {
                noMealsMessage.visibility = View.VISIBLE
            } else {
                noMealsMessage.visibility = View.GONE
                val sortedMeals = meals.sorted()

                sortedMeals.forEach { meal ->
                    val mealView = layoutInflater.inflate(R.layout.meal_item, mealContainer, false)
                    val mealName: TextView = mealView.findViewById(R.id.mealName)
                    val mealDate: TextView = mealView.findViewById(R.id.mealDate)
                    val mealTime: TextView = mealView.findViewById(R.id.mealTime)
                    val mealCalories: TextView = mealView.findViewById(R.id.mealCalories)
                    val mealImage: ImageView = mealView.findViewById(R.id.mealImage)
                    val editButton: Button = mealView.findViewById(R.id.editButton)
                    val deleteButton: Button = mealView.findViewById(R.id.deleteButton)

                    mealName.text = meal.name
                    mealDate.text = meal.date
                    mealTime.text = meal.time
                    mealCalories.text = "${meal.calories} Kcal"
                    // meal.image를 URL 또는 리소스 ID로 임시사용
                    // Glide.with(this).load(meal.image).into(mealImage)

                    editButton.setOnClickListener {
                        showEditDialog(meal)
                    }
                    deleteButton.setOnClickListener {
                        deleteMeal(meal)
                    }

                    mealContainer.addView(mealView)
                }
            }
        })
    }

    private fun showEditDialog(meal: Meal) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_meal, null)
        val mealNameEditText: EditText = dialogView.findViewById(R.id.editMealName)
        val mealCaloriesEditText: EditText = dialogView.findViewById(R.id.editMealCalories)
        val mealTimePicker: TimePicker = dialogView.findViewById(R.id.editMealTime)
        val saveButton: Button = dialogView.findViewById(R.id.saveButton)

        mealNameEditText.setText(meal.name)
        mealCaloriesEditText.setText(meal.calories)

        // 시간 설정
        val timeParts = meal.time.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].split(" ")[0].toInt()
        val isPM = timeParts[1].split(" ")[1].equals("PM", ignoreCase = true)

        mealTimePicker.setIs24HourView(false)
        mealTimePicker.hour = if (isPM && hour != 12) hour + 12 else hour
        mealTimePicker.minute = minute

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setPositiveButton("수정", null)
            .setNegativeButton("취소", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                meal.name = mealNameEditText.text.toString()
                meal.calories = mealCaloriesEditText.text.toString()

                val selectedHour = mealTimePicker.hour
                val selectedMinute = mealTimePicker.minute
                val amPm = if (selectedHour >= 12) "PM" else "AM"
                val formattedHour = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                meal.time = String.format("%02d:%02d %s", formattedHour, selectedMinute, amPm)

                saveButton.visibility = View.VISIBLE
                positiveButton.visibility = View.GONE
            }

            saveButton.setOnClickListener {
                updateMeal(meal)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun updateMeal(meal: Meal) {
        foodRecordViewModel.updateMeal(meal)
        Toast.makeText(requireContext(), "${meal.name} (으)로 수정되었습니다!", Toast.LENGTH_SHORT).show()
    }

    private fun deleteMeal(meal: Meal) {
        foodRecordViewModel.deleteMeal(meal)
        Toast.makeText(requireContext(), "${meal.name} (이)가 삭제되었습니다!", Toast.LENGTH_SHORT).show()
    }
}
