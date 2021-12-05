package com.example.schedule.dataClasses

import com.example.schedule.viewModel.MainActivityViewModel

data class Schedule(
    val id: Int,
    val name: String,
    val organization: Organization,
    var days: List<Day>
)
