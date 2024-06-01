package com.example.mc_jjikdan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.mc_jjikdan.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        loadProfile()

        binding.btnChangePhoto.setOnClickListener {
            openGallery()
        }

        binding.btnSaveProfile.setOnClickListener {
            saveProfile()
        }

        return binding.root
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                imageUri = intent?.data
                binding.ivProfileImage.setImageURI(imageUri)
            }
        }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startForResult.launch(intent)
    }

    private fun loadProfile() {
        val sharedPreferences = requireActivity().getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        binding.etNickname.setText(sharedPreferences.getString("nickname", ""))
        binding.etHeight.setText(sharedPreferences.getString("height", ""))
        binding.etWeight.setText(sharedPreferences.getString("weight", ""))
        binding.etDescription.setText(sharedPreferences.getString("description", ""))
        val imageUriString = sharedPreferences.getString("imageUri", null)
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString)
            binding.ivProfileImage.setImageURI(imageUri)
        }
    }

    private fun saveProfile() {
        val nickname = binding.etNickname.text.toString()
        val height = binding.etHeight.text.toString()
        val weight = binding.etWeight.text.toString()
        val description = binding.etDescription.text.toString()

        val sharedPreferences = requireActivity().getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("nickname", nickname)
            putString("height", height)
            putString("weight", weight)
            putString("description", description)
            putString("imageUri", imageUri?.toString())
            apply()
        }

        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
