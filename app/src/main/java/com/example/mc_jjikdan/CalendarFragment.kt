package com.example.mc_jjikdan

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mc_jjikdan.databinding.FragmentCalendarBinding
import com.example.mc_jjikdan.databinding.MealItemBinding
import com.example.mc_jjikdan.viewmodel.DiaryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private val viewModel: DiaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nickname = getNicknameFromPreferences()

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val formattedMonth = String.format("%02d", month + 1)
            val formattedDayOfMonth = String.format("%02d", dayOfMonth)
            val date = "$year-$formattedMonth-$formattedDayOfMonth"
            viewModel.getData(nickname, date)

            viewModel.diary.observe(viewLifecycleOwner) { diary ->
                binding.mealContainer.removeAllViews()
                if (diary != null && diary.diary_menus.isNotEmpty()) {
                    diary.diary_menus.forEach { meal ->
                        addMealView(meal)
                    }
                    binding.noMealsMessage.visibility = View.GONE
                } else {
                    binding.noMealsMessage.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun addMealView(meal: Meal) {
        val inflater = LayoutInflater.from(requireContext())
        val mealBinding = MealItemBinding.inflate(inflater, binding.mealContainer, false)

        mealBinding.mealName.text = meal.name
        mealBinding.mealDate.text = meal.date
        mealBinding.mealTime.text = meal.time
        mealBinding.mealCalories.text = "${meal.calories} Kcal"
        // Uncomment and modify the line below if you have an image URL or resource
        // Glide.with(this).load(meal.image).into(mealBinding.mealImage)

        mealBinding.editButton.setOnClickListener {
            showEditDialog(meal)
        }
        mealBinding.deleteButton.setOnClickListener {
            deleteMeal(meal)
        }

        binding.mealContainer.addView(mealBinding.root)
    }

    private fun showEditDialog(meal: Meal) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_meal, null)
        val mealNameEditText: EditText = dialogView.findViewById(R.id.editMealName)
        val mealCaloriesEditText: EditText = dialogView.findViewById(R.id.editMealCalories)
        val mealTimePicker: TimePicker = dialogView.findViewById(R.id.editMealTime)
        val saveButton: Button = dialogView.findViewById(R.id.saveButton)

        mealNameEditText.setText(meal.name)
        mealCaloriesEditText.setText(meal.calories)

        // Set time picker
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
        viewModel.updateMeal(meal)
        Toast.makeText(requireContext(), "${meal.name}이(가) 수정되었습니다!", Toast.LENGTH_SHORT).show()
    }

    private fun deleteMeal(meal: Meal) {
        viewModel.deleteMeal(meal)
        Toast.makeText(requireContext(), "${meal.name}이(가) 삭제되었습니다!", Toast.LENGTH_SHORT).show()
    }

    private fun getNicknameFromPreferences(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        return sharedPreferences.getString("nickname", "") ?: ""
    }
}