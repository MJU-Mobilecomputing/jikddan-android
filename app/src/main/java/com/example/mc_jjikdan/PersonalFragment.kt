package com.example.mc_jjikdan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mc_jjikdan.databinding.FragmentPersonalBinding
import java.text.SimpleDateFormat
import java.util.*

class PersonalFragment : Fragment() {

    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!

    private lateinit var foodRecordViewModel: FoodRecord
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        foodRecordViewModel = ViewModelProvider(requireActivity()).get(FoodRecord::class.java)

        // 초기 주간 데이터 로드
        loadCurrentWeekMeals()

        // 주별 화살표 버튼 클릭 리스너
        binding.leftArrow.setOnClickListener {
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            loadCurrentWeekMeals()
        }

        binding.rightArrow.setOnClickListener {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            loadCurrentWeekMeals()
        }

        // 주간 식단 데이터 관찰
        foodRecordViewModel.weeklySummaryLiveData.observe(viewLifecycleOwner) { summary ->
            summary?.let {
                updateUI(it)
            }
        }

        binding.btnViewSolution.setOnClickListener {
            binding.solutionLayout.visibility = View.VISIBLE
        }
        return binding.root
    }

    private fun loadCurrentWeekMeals() {
        // 주의 시작일 계산
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val startDate = calendar.time

        // 주의 종료일 계산
        calendar.add(Calendar.DAY_OF_WEEK, 6) // 주의 종료일은 시작일로부터 6일 후
        val endDate = calendar.time

        // 주차 계산
        calendar.time = startDate
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)
        val weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)

        // 주가 다음 달로 넘어가는지 확인
        calendar.time = endDate
        val endMonth = calendar.get(Calendar.MONTH) + 1
        val endYear = calendar.get(Calendar.YEAR)

        val month = if (currentMonth != endMonth || currentYear != endYear) {
            endMonth
        } else {
            currentMonth
        }

        val weekNum = if (currentMonth != endMonth || currentYear != endYear) {
            1
        } else {
            weekOfMonth
        }

        foodRecordViewModel.getWeeklySummary(month, weekNum)

        // 주 텍스트 업데이트
        val weekText = dateFormat.format(startDate) + " - " +
                dateFormat.format(endDate)
        binding.weekText.text = weekText

        // 주차 텍스트 업데이트
        val monthText = SimpleDateFormat("M월", Locale.getDefault()).format(calendar.time)
        binding.textViewTitle.text = "$monthText ${weekNum}번째 주 식단 분석"
    }


    private fun updateUI(summary: WeeklySummary) {
        binding.progressKcal.progress = summary.weeklyFoodMoisture
        binding.progressCarbohydrate.progress = summary.weeklyCarbon
        binding.progressFat.progress = summary.weeklyFat
        binding.progressProtein.progress = summary.weeklyProtein
        binding.solutionScore.text = "${summary.weeklyAverageScore}점"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
