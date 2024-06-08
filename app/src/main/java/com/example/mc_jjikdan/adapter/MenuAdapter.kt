package com.example.mc_jjikdan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mc_jjikdan.api.diary.dto.Menu
import com.example.mc_jjikdan.databinding.MealItemBinding

class MenuAdapter(private val data: MutableList<Menu>) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(val binding: MealItemBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder(MealItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val binding = (holder as MenuViewHolder).binding
        Glide.with(binding.mealImage).load(data[position].img).into(binding.mealImage)
        binding.mealDate.text = data[position].date
        binding.mealTime.text = data[position].created_at.slice(11..<data[position].created_at.length - 10)
        binding.mealCalories.text = data[position].total_cal.toString()
        binding.mealName.text = data[position].summary
    }
}