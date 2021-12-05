package com.example.schedule.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.schedule.R
import com.example.schedule.dataClasses.Status
import com.example.schedule.dataClasses.Type
import com.example.schedule.databinding.RecordBinding
import com.example.schedule.databinding.SetRecordBinding
import com.example.schedule.models.Interval
import java.text.SimpleDateFormat
import java.util.*

class RecordAdapter(
    private val context: Context,
    private var intervals: List<Interval>,
    private var statuses: List<Status>,
    private var types: List<Type>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class RecordViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = RecordBinding.bind(item)

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(interval: Interval) {
            when (interval.type) {
                1 -> {
                    binding.statusAppointment.text = "Свободно"
                    binding.topLine.setBackgroundColor(Color.WHITE)
                }
                2 -> {
                    binding.statusAppointment.text = "Смена снята"
                    binding.recordCard.setBackgroundColor(context
                        .resources
                        .getColor(R.color.closeShiftBackground, null))
                    binding.topLine.setBackgroundColor(context
                        .resources
                        .getColor(R.color.closeShiftLine, null))
                }
                3 -> {
                    binding.statusAppointment.text = "Нет смены"
                    binding.recordCard.setBackgroundColor(context
                        .resources
                        .getColor(R.color.noShiftBackground, null))
                    binding.topLine.setBackgroundColor(context
                        .resources
                        .getColor(R.color.noShiftLine, null))
                }
            }

            val start: Calendar =
                GregorianCalendar(1970, 1, 1,
                    (interval.start / 3600),
                    (interval.start % 3600 / 60))
            val end: Calendar =
                GregorianCalendar(1970, 1, 1,
                    (interval.end / 3600),
                    (interval.end % 3600 / 60))

            val timeFormat = SimpleDateFormat("HH:mm")
            binding.range.text = "${timeFormat.format(start.time)} - ${timeFormat.format(end.time)}"
        }
    }

    inner class SetRecordViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = SetRecordBinding.bind(item)

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind(interval: Interval) {
            val record = interval.record!!
            println(statuses)
            val status = statuses.find { it.id == record.status }

            val recordType = types.find { it.id == record.types }
            val duration: Int = record.end - record.start
            if (duration >= 3600) {
                val hour = duration / 3600
                val min = (duration % 3600) / 60
                if (min == 0) binding.range.text = "$hour ч"
                else binding.range.text = "$hour ч $min мин"

            } else binding.range.text = "${(duration % 3600) / 60} мин"
            binding.fullName.text = record.pacient?.name
            val start: Calendar =
                GregorianCalendar(1970, 1, 1,
                    (record.start / 3600),
                    (record.start % 3600 / 60))
            val timeFormat = SimpleDateFormat("HH:mm")
            binding.time.text = timeFormat.format(start.time)

            when (recordType?.name) {
                "Получение справки" -> binding.topLine.setBackgroundColor(context
                    .resources
                    .getColor(R.color.blueLine, null))
                "Плановый осмотр" -> binding.topLine.setBackgroundColor(context
                    .resources
                    .getColor(R.color.greenLine, null))
                "Экстренная ситуация" -> binding.topLine.setBackgroundColor(context
                    .resources
                    .getColor(R.color.redLine, null))
            }
            if (status != null) {
                if (status.name == "Ожидает подтверждения")
                    binding.recordCard.setBackgroundColor(context
                        .resources
                        .getColor(R.color.blueBackground, null))
                else
                    binding.recordCard.setBackgroundColor(context
                        .resources
                        .getColor(R.color.greenBackground, null))
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val type1 =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.set_record, parent, false)
        val type2 = LayoutInflater.from(parent.context)
            .inflate(R.layout.record, parent, false)
        return when (viewType) {
            1 -> SetRecordViewHolder(type1)
            0 -> RecordViewHolder(type2)
            else -> RecordViewHolder(type1)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RecordViewHolder -> holder.bind(intervals[position])
            is SetRecordViewHolder -> holder.bind(intervals[position])
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (intervals[position].type) {
            1, 2, 3 -> 0
            4 -> 1
            else -> 0
        }
    }

    override fun getItemCount(): Int {
        return intervals.size
    }
}