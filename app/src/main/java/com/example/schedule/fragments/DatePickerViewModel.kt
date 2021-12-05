package com.example.schedule.fragments

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schedule.viewModel.MainActivityViewModel
import java.text.SimpleDateFormat
import java.util.*

class DatePickerViewModel:ViewModel() {

    private val pickedDay:MutableLiveData<Calendar> by lazy {
        MutableLiveData<Calendar>()
    }
    val pickedRange:MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }
    val startOfWeek:MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }
    val endOfWeek:MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }
    fun onDateSetViewModel(year: Int, month: Int, day: Int){
        val c:Calendar = GregorianCalendar(year,month,day)
        pickedDay.value = c
    }
    @SuppressLint("SimpleDateFormat")
    fun getDateWithStyle(isStartActivity:Boolean){
        val dateFormat = SimpleDateFormat("d MMM")
        val buffCalendar = Calendar.getInstance(TimeZone.getDefault())
        val c:Calendar = if(isStartActivity){
            GregorianCalendar(buffCalendar.get(Calendar.YEAR),
                buffCalendar.get(Calendar.MONTH),
                buffCalendar.get(Calendar.DAY_OF_MONTH))
        } else pickedDay.value!!


        if (c.get(Calendar.DAY_OF_WEEK) == 1){
            c.add(Calendar.DAY_OF_MONTH,- 6)
        } else c.add(Calendar.DAY_OF_MONTH,- (c.get(Calendar.DAY_OF_WEEK) - 2))
        startOfWeek.value = c.timeInMillis
        val startWeek = dateFormat.format(c.time)
        c.add(Calendar.DAY_OF_MONTH,+ 6)
        endOfWeek.value = c.timeInMillis
        val endWeek = dateFormat.format(c.time)
        pickedRange.value = "$startWeek - $endWeek\n${c.get(Calendar.YEAR)}"

    }



}