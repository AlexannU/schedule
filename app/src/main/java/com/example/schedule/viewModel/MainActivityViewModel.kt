package com.example.schedule.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schedule.adapters.RecordAdapter
import com.example.schedule.dataClasses.*
import com.example.schedule.fragments.DatePickerViewModel
import com.example.schedule.models.Interval
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.*
import java.io.IOException
import java.util.*

class MainActivityViewModel : ViewModel() {

    private val baseUrl:String = "http://192.168.31.238:3000/"
    val schedule: MutableLiveData<Schedule> by lazy {
        MutableLiveData<Schedule>()
    }
    val periods: MutableLiveData<List<Period>> by lazy{
        MutableLiveData<List<Period>>()
    }
    val statuses:MutableLiveData<List<Status>> by lazy{
        MutableLiveData<List<Status>>()
    }
    val types:MutableLiveData<List<Type>> by lazy{
        MutableLiveData<List<Type>>()
    }
    val filterSchedule: MutableLiveData<List<Day>> by lazy {
        MutableLiveData<List<Day>>()
    }
    val isAllDataCome:MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    private var isPeriodCome:Boolean = false
    private var isStatusesCome:Boolean = false
    private var isScheduleCome:Boolean = false
    private var isTypesCome:Boolean = false

    fun confirmExecute(){
        if(isPeriodCome && isStatusesCome && isScheduleCome && isTypesCome){
            isAllDataCome.postValue(true)
        }
    }
    private fun getTypes(client: OkHttpClient){
        val url =  baseUrl + "types"
        val request: Request = Request.Builder()
            .url(url)
            .build()


        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful){
                    val res = response.body?.string()
                    types.postValue(Gson().fromJson(res,Array<Type>::class.java).toList())
                    isTypesCome = true
                    confirmExecute()
                }
            }

        })
    }
    private fun getPeriods(client: OkHttpClient){
        val url =  baseUrl + "periods"
        val request: Request = Request.Builder()
            .url(url)
            .build()


        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful){
                    val res = response.body?.string()
                    periods.postValue(Gson().fromJson(res,Array<Period>::class.java).toList())
                    isPeriodCome = true
                    confirmExecute()
                }
            }

        })
    }
    private fun getStatuses(client: OkHttpClient){
        val url = baseUrl + "statuses"
        val request: Request = Request.Builder()
            .url(url)
            .build()


        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful){
                    val res = response.body?.string()
                    statuses.postValue(Gson().fromJson(res,Array<Status>::class.java).toList())
                    isStatusesCome = true
                    confirmExecute()
                }
            }

        })
    }
    fun getSchedule() {
        val client = OkHttpClient()
        getStatuses(client)
        getPeriods(client)
        getTypes(client)
        val url = baseUrl + "schedule"
        val request: Request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val myRes = response.body?.string()
                    println(myRes)
                    var res = Gson().fromJson(myRes, Schedule::class.java)
                    res.days = res.days.sortedBy { day -> day.date }
                    schedule.postValue(res)
                    isScheduleCome = true
                    confirmExecute()
                }
            }

        })

    }

    fun filterDataForPickedDate(startOfWeek: Long, endOfWeek: Long) {
        if (schedule.value != null) {
            val selectedWeek = schedule.value!!.days.filter { day ->
                val cal: Calendar = Calendar.getInstance(TimeZone.getDefault())
                cal.timeInMillis = day.date
                cal.timeInMillis in startOfWeek..endOfWeek

            }
            filterSchedule.value = selectedWeek
        }

    }

    fun generateInterval(records: List<Record>,periods:List<Period>) :List<Interval>{
        val sortedRecords = records.sortedBy { start -> start.start }
        val interval: MutableList<Interval> = mutableListOf()
        if(periods.isEmpty()){
            interval.add(Interval(12 * 3600, 21 *3600, null, Interval.NO_SHIFT))
        }
        for ((i,period) in periods.withIndex()){
            if(i == 0){
                if (period.start != 12 * 3600) {
                    interval.add(Interval(12 * 3600, period.start, null, Interval.NO_SHIFT))
                }
            } else {
                interval.add(Interval(periods[i-1].end, period.start, null, Interval.NO_SHIFT))
            }

            for ((j, record) in sortedRecords.withIndex()) {
                if(record.start >= period.start && record.end <= period.end){
                    if (j == 0) {
                        if (period.start == record.start) {
                            interval.add(Interval(record.start, record.end, record, Interval.RECORD))
                        } else {
                            interval.add(Interval(period.start, record.start, null, Interval.FREE))
                            interval.add(Interval(record.start, record.end, record, Interval.RECORD))
                        }
                    } else {
                        if (interval[interval.lastIndex].end == record.start) {
                            interval.add(Interval(record.start, record.end, record, Interval.RECORD))
                        } else {
                            interval.add(Interval(interval[interval.lastIndex].end,
                                record.start,
                                null,
                                Interval.FREE))
                            interval.add(Interval(record.start, record.end, record, Interval.RECORD))
                        }
                    }
                }

            }
            if (interval[interval.lastIndex].end < period.end){
                interval.add(Interval(interval[interval.lastIndex].end, period.end, null, Interval.FREE))
            }

            if (period.end < 21 * 3600 && i == periods.lastIndex){
                interval.add(Interval(period.end,21 * 3600,null,Interval.NO_SHIFT))
            }

        }
        return interval
    }
    fun filterPeriod(periodsId:List<Int>): List<Period>{
        return periods.value!!.filter { period -> periodsId.contains(period.id)  }
    }
    fun getCalendarFromMilliseconds(millis: Long):Calendar{
        val calendar:Calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.timeInMillis = millis
        return calendar
    }


}