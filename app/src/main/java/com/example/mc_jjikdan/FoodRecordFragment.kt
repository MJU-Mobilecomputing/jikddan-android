package com.example.mc_jjikdan

import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mc_jjikdan.api.diary.dto.Menu
import com.example.mc_jjikdan.databinding.FragmentFoodRecordBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FoodRecordFragment : Fragment() {
    private var _binding: FragmentFoodRecordBinding? = null
    private val binding get() = _binding!!
    private lateinit var foodRecordViewModel: FoodRecord
    private val calendar = Calendar.getInstance()
    private lateinit var selectedDate: String
    private lateinit var selectedMenuTime: String
    private var imageUri: Uri? = null

    private lateinit var launcher: ActivityResultLauncher<Intent>

    // 하루 권장량 정의
    private val dailyRecommendedWater = 2000 // 2L per day
    private val dailyRecommendedSalt = 2300 // 2300mg per day
    private val dailyRecommendedKcal = 2000 // 2000 kcal per day (평균적인 하루 칼로리 권장량)

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val REQUEST_CODE_PERMISSIONS = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodRecordBinding.inflate(inflater, container, false)
        foodRecordViewModel = ViewModelProvider(requireActivity()).get(FoodRecord::class.java)

        // 초기 날짜 설정
        binding.calendarView.date = Calendar.getInstance().timeInMillis
        selectedDate = getCurrentDateString()

        // 날짜 선택 리스너
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = getCurrentDateString()
        }

        // Spinner 설정
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.menu_times,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMenuTime.adapter = adapter

        binding.spinnerMenuTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedMenuTime = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // 사진 업로드 버튼 리스너
        binding.btnUploadImage.setOnClickListener {
            if (checkCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }

        // 식단 저장 버튼 리스너
        binding.btnSaveFood.setOnClickListener {
            if (imageUri != null) {
                val nickname = getNicknameFromPreferences()
                uploadMeal(nickname, imageUri!!)
            } else {
                Toast.makeText(requireContext(), "이미지를 선택하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imageUri = result.data?.data
                binding.btnUploadImage.text = "이미지 선택됨"
            }
        }

        return binding.root
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.CAMERA),
            REQUEST_CODE_PERMISSIONS
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            val photo = data?.extras?.get("data") as Bitmap
            imageUri = saveImageToInternalStorage(photo)
            binding.btnUploadImage.text = "이미지 선택됨"
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val file = File(requireContext().filesDir, "temp_image.jpg")
        try {
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.fromFile(file)
    }

    private fun uploadMeal(nickname: String, uri: Uri) {
        val file = File(uri.path)
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("img", file.name, requestFile)
        val datePart = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), selectedDate)
        val menuTimePart = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), selectedMenuTime)

        RetrofitClient.apiService.uploadMeal(nickname, body, datePart, menuTimePart).enqueue(object : Callback<Menu> {
            override fun onResponse(call: Call<Menu>, response: Response<Menu>) {
                if (response.isSuccessful) {
                    val mealResponse = response.body()
                    Log.d(TAG, "Menu: $mealResponse") // 로그 추가
                    // 분석된 값 업데이트
                    updateUI(mealResponse)
                    Toast.makeText(requireContext(), "식단이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG, "Error: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "식단 저장 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Menu>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(menuResponse: Menu?) {
        menuResponse?.let {
            binding.linearLayoutStats.visibility = View.VISIBLE
            // 칼로리 업데이트 및 퍼센트 계산
            val kcalPercent = (it.total_cal.toDouble() / dailyRecommendedKcal * 100).toInt()
            binding.txtKcalAmount.text = "${it.total_cal} kcal"
            binding.progressKcal.progress = kcalPercent
            binding.txtKcalPercent.text = "$kcalPercent% / 하루 권장량"

            // 나트륨 섭취량 업데이트 및 퍼센트 계산
            val saltPercent = (it.salt.toDouble() / dailyRecommendedSalt * 100).toInt()
            binding.txtSaltAmount.text = "${it.salt} mg"
            binding.progressSodium.progress = saltPercent
            binding.txtSaltPercent.text = "$saltPercent% / 하루 권장량"

            // 수분 섭취량 업데이트 및 퍼센트 계산
            val waterPercent = (it.food_moisture.toDouble() / dailyRecommendedWater * 100).toInt()
            binding.txtWaterAmount.text = "${it.food_moisture} ml"
            binding.progressWater.progress = waterPercent
            binding.txtWaterPercent.text = "$waterPercent% / 하루 권장량"
        }
    }

    private fun getNicknameFromPreferences(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        return sharedPreferences.getString("nickname", "") ?: ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
