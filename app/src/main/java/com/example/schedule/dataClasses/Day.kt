package com.example.schedule.dataClasses

import com.example.schedule.viewModel.MainActivityViewModel

data class Day(
    val id: Int,
    val date: Long,
    val available_periods: List<Int>,
    val records: List<Record>,
)
