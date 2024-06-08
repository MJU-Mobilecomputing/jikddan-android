package com.example.mc_jjikdan

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        binding.btnEditProfile.setOnClickListener {
            navigateToEditProfile()
        }

        binding.btnDeleteProfile.setOnClickListener {
            deleteProfile()
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

    private fun navigateToEditProfile() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, EditProfileFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun deleteProfile() {
        val sharedPreferences = requireActivity().getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
        binding.tvNickname.text = ""
        binding.tvHeight.text = ""
        binding.tvWeight.text = ""
        binding.tvDescription.text = ""
        binding.ivProfileImage.setImageResource(R.drawable.ic_launcher_background)
        binding.btnEditProfile.text = "프로필 입력"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
