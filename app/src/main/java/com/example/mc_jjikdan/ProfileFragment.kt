import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.mc_jjikdan.EditProfileFragment
import com.example.mc_jjikdan.R
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
            showDeleteConfirmationDialog()
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
        parentFragmentManager.commit {
            replace(R.id.fragment_container, EditProfileFragment())
            addToBackStack(null)
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(context)
            .setTitle("회원 삭제")
            .setMessage("회원 삭제를 하면 전체 데이터가 사라집니다. 정말 삭제하시겠습니까?")
            .setPositiveButton("예") { _, _ -> deleteProfile() }
            .setNegativeButton("아니오", null)
            .show()
    }

    private fun deleteProfile() {
        val sharedPreferences = requireActivity().getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        val nickname = sharedPreferences.getString("nickname", "") ?: ""

        // SharedPreferences에서 사용자 데이터 삭제
        val userPreferences = requireActivity().getSharedPreferences("UserData_$nickname", Context.MODE_PRIVATE)
        with(userPreferences.edit()) {
            clear()
            apply()
        }

        // 프로필 데이터 삭제
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

        // 솔루션 레이아웃 숨기기
        binding.solutionLayout.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
