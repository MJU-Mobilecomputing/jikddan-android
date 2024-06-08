package com.example.mc_jjikdan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mc_jjikdan.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_calendar -> {
                    loadFragment(CalendarFragment())
                    true
                }
                R.id.nav_food_record -> {
                    //loadFragment(FoodRecordFragment())
                    true
                }
                R.id.nav_personal -> {
                    loadFragment(PersonalFragment())
                    true
                }
                R.id.nav_profile -> {
                    //loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
        // 초기 화면 로드: 홈 Fragment
        loadFragment(HomeFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
