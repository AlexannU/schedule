package com.example.schedule

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.schedule.adapters.RecordAdapter
import com.example.schedule.dataClasses.Period
import com.example.schedule.dataClasses.Record
import com.example.schedule.dataClasses.Status
import com.example.schedule.databinding.ActivityMainBinding
import com.example.schedule.fragments.DatePickerFragment
import com.example.schedule.fragments.DatePickerViewModel
import com.example.schedule.models.Interval
import com.example.schedule.viewModel.MainActivityViewModel
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val datePickerViewModel: DatePickerViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainDate.setOnClickListener {
            DatePickerFragment().show(supportFragmentManager, "datePicker")
        }
        datePickerViewModel.getDateWithStyle(true)

        datePickerViewModel.pickedRange.observe(this, {
            binding.mainDate.text = it
        })

        mainActivityViewModel.getSchedule()

        mainActivityViewModel.isAllDataCome.observe(this, {
            if (it) {

                mainActivityViewModel.filterDataForPickedDate(datePickerViewModel.startOfWeek.value!!,
                    datePickerViewModel.endOfWeek.value!!)
            }
        })

        mainActivityViewModel.filterSchedule.observe(this, {

            if (binding.scheduleOnWeek.childCount > 0) binding.scheduleOnWeek.removeAllViews()

            for (day in it) {

                val periods = mainActivityViewModel.filterPeriod(day.available_periods)

                val intervalList = mainActivityViewModel.generateInterval(day.records, periods)

                val dateInCalendar = mainActivityViewModel.getCalendarFromMilliseconds(day.date)

                val scheduleOneDay: View = LayoutInflater.from(this)
                    .inflate(R.layout.schedule_on_day, binding.root, false)
                val recyclerView: RecyclerView =
                    scheduleOneDay.findViewById(R.id.appointmentRecyclerView)
                val dayLine: View = scheduleOneDay.findViewById(R.id.currentDayLine)
                val dayOfTheWeek: TextView = scheduleOneDay.findViewById(R.id.daysOfTheWeek)
                val dayTextView: TextView = scheduleOneDay.findViewById(R.id.day)

                if (Calendar.getInstance(TimeZone.getDefault()).get(Calendar.DAY_OF_MONTH) ==
                    dateInCalendar.get(Calendar.DAY_OF_MONTH)
                ) {
                    dayLine.setBackgroundColor(resources.getColor(R.color.currentDayLineColor,
                        null))
                }
                val weekDateFormat = SimpleDateFormat("EE")
                dayOfTheWeek.text = weekDateFormat.format(dateInCalendar.time)
                dayTextView.text = dateInCalendar.get(Calendar.DAY_OF_MONTH).toString()
                recyclerView.layoutManager = LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL,
                    false)

                val adapter = RecordAdapter(this,
                    intervalList,
                    mainActivityViewModel.statuses.value!!,
                    mainActivityViewModel.types.value!!)
                recyclerView.adapter = adapter
                binding.scheduleOnWeek.addView(scheduleOneDay)
            }
        })


    }


}