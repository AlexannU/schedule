package com.example.schedule.models

import com.example.schedule.dataClasses.Period
import com.example.schedule.dataClasses.Record

class Interval(var start:Int,var end:Int,var record: Record?,var type:Int) {
    companion object{
        const val FREE:Int = 1
        const val CLOSE:Int = 2
        const val NO_SHIFT:Int = 3
        const val RECORD:Int = 4
    }



}