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

    // 일주일 권장량 정의
    private val weeklyRecommendedWater = 14000 // 2L * 7 days
    private val weeklyRecommendedSalt = 3500 // 500mg * 7 days
    private val weeklyRecommendedCarbohydrate = 2100 // 300g * 7 days
    private val weeklyRecommendedProtein = 350 // 50g * 7 days
    private val weeklyRecommendedFat = 560 // 80g * 7 days

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

        // 솔루션 데이터 관찰
        foodRecordViewModel.solutionLiveData.observe(viewLifecycleOwner) { solution ->
            solution?.let {
                binding.textViewSolutionDescription.text = it
                binding.progressSolution.progress = foodRecordViewModel.weeklySummaryLiveData.value?.weeklyAverageScore ?: 0
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
        binding.textViewSolutionTitle.text = "$monthText ${weekNum}번째 주 식단 솔루션"
    }

    private fun updateUI(summary: WeeklySummary) {
        binding.linearLayoutStats.visibility = View.VISIBLE

        // 수분 섭취량 업데이트 및 퍼센트 계산
        val waterPercent = (summary.weeklyFoodMoisture.toDouble() / weeklyRecommendedWater * 100).toInt()
        binding.txtWaterAmount.text = "${summary.weeklyFoodMoisture} ml"
        binding.progressWater.progress = waterPercent
        binding.txtWaterPercent.text = "$waterPercent% / 일주일 권장량"

        // 나트륨 섭취량 업데이트 및 퍼센트 계산
        val saltPercent = (summary.weeklySalt.toDouble() / weeklyRecommendedSalt * 100).toInt()
        binding.txtSaltAmount.text = "${summary.weeklySalt} mg"
        binding.progressSodium.progress = saltPercent
        binding.txtSaltPercent.text = "$saltPercent% / 일주일 권장량"

        // 탄수화물 섭취량 업데이트 및 퍼센트 계산
        val carbohydratePercent = (summary.weeklyCarbon.toDouble() / weeklyRecommendedCarbohydrate * 100).toInt()
        binding.txtCarbAmount.text = "${summary.weeklyCarbon} g"
        binding.progressCarbohydrate.progress = carbohydratePercent
        binding.txtCarbPercent.text = "$carbohydratePercent% / 일주일 권장량"

        // 단백질 섭취량 업데이트 및 퍼센트 계산
        val proteinPercent = (summary.weeklyProtein.toDouble() / weeklyRecommendedProtein * 100).toInt()
        binding.txtProteinAmount.text = "${summary.weeklyProtein} g"
        binding.progressProtein.progress = proteinPercent
        binding.txtProteinPercent.text = "$proteinPercent% / 일주일 권장량"

        // 지방 섭취량 업데이트 및 퍼센트 계산
        val fatPercent = (summary.weeklyFat.toDouble() / weeklyRecommendedFat * 100).toInt()
        binding.txtFatAmount.text = "${summary.weeklyFat} g"
        binding.progressFat.progress = fatPercent
        binding.txtFatPercent.text = "$fatPercent% / 일주일 권장량"

        binding.solutionScore.text = "${summary.weeklyAverageScore}점"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
