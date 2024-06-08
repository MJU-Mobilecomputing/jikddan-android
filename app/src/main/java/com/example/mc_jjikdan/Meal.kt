package com.example.mc_jjikdan

data class Meal(
    var name: String,
    val date: String,
    var time: String,
    var calories: String,
    val image: String
) : Comparable<Meal> {
    override fun compareTo(other: Meal): Int {
        val thisTime = parseTime(this.time)
        val otherTime = parseTime(other.time)
        return thisTime.compareTo(otherTime)
    }

    private fun parseTime(time: String): Int {
        val parts = time.split(" ")
        val timeParts = parts[0].split(":").map { it.toInt() }
        val isPM = parts[1].equals("PM", ignoreCase = true)
        val hour = if (isPM && timeParts[0] != 12) timeParts[0] + 12 else timeParts[0]
        return hour * 60 + timeParts[1]
    }
}
