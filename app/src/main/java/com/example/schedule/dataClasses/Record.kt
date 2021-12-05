package com.example.schedule.dataClasses

import com.example.schedule.viewModel.MainActivityViewModel

data class Record(
    val id: Int?,
    val status: Int,
    val types: Int,
    val date: Long?,
    val start: Int,
    val end: Int,
    val pacient: Pacient?,
    val reason: String?,
    val room: Room?
)
