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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mc_jjikdan.adapter.MenuAdapter
import com.example.mc_jjikdan.api.diary.dto.Menu
import com.example.mc_jjikdan.databinding.FragmentCalendarBinding
import com.example.mc_jjikdan.viewmodel.DiaryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private lateinit var calendarView: CalendarView
    private lateinit var mealContainer: LinearLayout
    private lateinit var foodRecordViewModel: FoodRecord
    private lateinit var noMealsMessage: TextView
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
            val data = mutableListOf<Menu>()
            val formattedMonth = String.format("%02d", month + 1)
            val formattedDayOfMonth = String.format("%02d", dayOfMonth)
            val date = "$year-$formattedMonth-$formattedDayOfMonth"
            viewModel.getData(nickname, date)
            binding.menuListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            viewModel.diary.observe(viewLifecycleOwner) { diary ->
                if (diary != null) {
                    data.clear()
                    for (menu in diary.diary_menus) {
                        data.add(menu)
                    }
                    binding.menuListRecyclerView.adapter = MenuAdapter(data)
                } else {
                    Log.e("CalendarFragment", "Diary data is null")
                }
            }
        }
    }

    private fun getNicknameFromPreferences(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        return sharedPreferences.getString("nickname", "") ?: ""
    }
}
