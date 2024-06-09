package com.example.mc_jjikdan

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.mc_jjikdan.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        loadProfile()
        loadSolution()

        binding.btnEditProfile.setOnClickListener {
            navigateToEditProfile()
        }

        return binding.root
    }

    private fun loadProfile() {
        val sharedPreferences = requireActivity().getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        val nickname = sharedPreferences.getString("nickname", "")
        val height = sharedPreferences.getString("height", "")
        val weight = sharedPreferences.getString("weight", "")
        val description = sharedPreferences.getString("description", "")
        val imageUriString = sharedPreferences.getString("imageUri", null)

        binding.tvNickname.text = nickname
        binding.tvHeight.text = "$height cm"
        binding.tvWeight.text = "$weight kg"
        binding.tvDescription.text = description
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            binding.ivProfileImage.setImageURI(imageUri)
        }
    }

    private fun loadSolution() {
        val sharedPreferences = requireActivity().getSharedPreferences("WeeklySummaryData", Context.MODE_PRIVATE)
        val weeklyAverageScore = sharedPreferences.getInt("weeklyAverageScore", 0)
        val solution = sharedPreferences.getString("solution", "데이터가 없습니다.")

        binding.textViewSolutionTitle.text = "이번 주 식단 솔루션"

        // weeklyAverageScore가 제대로 받아졌는지 확인하는 로그 출력
        println("weeklyAverageScore: $weeklyAverageScore") // 디버그 용 로그

        binding.solutionScore.text = "${weeklyAverageScore}점"  // 여기서 문제가 생길 수 있음
        binding.textViewSolutionDescription.text = solution
        binding.progressSolution.progress = weeklyAverageScore
    }

    private fun navigateToEditProfile() {
        parentFragmentManager.commit {
            replace(R.id.fragment_container, EditProfileFragment())
            addToBackStack(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
