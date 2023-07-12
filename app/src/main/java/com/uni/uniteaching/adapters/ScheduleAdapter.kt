package com.uni.uniteaching.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.uni.uniteaching.R

import com.uni.uniteaching.classes.ScheduleDataType

class ScheduleAdapter(
    val context: Context,
    var dataList: MutableList<ScheduleDataType>,
    val onItemClick:(ScheduleDataType)->Unit,
    val onAttendClicked: ( ScheduleDataType) -> Unit

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataList[position].type == VIEW_TYPE_ONE) VIEW_TYPE_ONE else VIEW_TYPE_TWO
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ONE) {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.section_item, parent, false)
            ViewHolder1(view)
        } else {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.lecture_item, parent, false)
            ViewHolder2(view)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = dataList[position]

        if (currentItem.type == VIEW_TYPE_TWO) {
            (holder as ViewHolder2)
            holder.courseName_l.text = currentItem.courseName
            holder.location_l.text = currentItem.hallID
            holder.lecturer.text = currentItem.professorName
            holder.day.text = currentItem.day
            holder.from_l.text = currentItem.time
            holder.to_l.text = currentItem.endTime

            if (currentItem.hasRunning) {
                holder.isRunning.text = "is running"
            } else {
                holder.isRunning.text = "not running"
            }


        } else {
            (holder as ViewHolder1)
            holder.courseName.text = currentItem.courseName
            holder.location.text = currentItem.hallID
            holder.assistant.text = currentItem.professorName
            holder.day.text = currentItem.day
            holder.from.text = currentItem.time
            holder.to.text = currentItem.endTime
            if (currentItem.hasRunning) {
                holder.isRunning.text = "is running"
            } else {
                holder.isRunning.text = "not running"
            }

        }

    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    fun update(list: MutableList<ScheduleDataType>) {
        this.dataList = list
        notifyDataSetChanged()
    }

    inner class ViewHolder1(item: View) : RecyclerView.ViewHolder(item) {
        val isRunning = item.findViewById<TextView>(R.id.sec_is_running)
        val courseName = item.findViewById<TextView>(R.id.course_name_s)
        val location = item.findViewById<TextView>(R.id.section_location)
        val assistant = item.findViewById<TextView>(R.id.section_assistant)
        val day = item.findViewById<TextView>(R.id.section_day)

        val from = item.findViewById<TextView>(R.id.from_time_tv)
        val to = item.findViewById<TextView>(R.id.to_time_tv)
        val attend = item.findViewById<CardView>(R.id.attend_card_lecture)

        val recyItem = item.findViewById<ConstraintLayout>(R.id.section_view)

        init {

            attend.setOnClickListener {
                onAttendClicked.invoke( dataList[adapterPosition])

            }
            recyItem.setOnClickListener{
                onItemClick.invoke(dataList[adapterPosition])
            }


        }


    }

    private inner class ViewHolder2(item: View) :
        RecyclerView.ViewHolder(item) {
        val isRunning = item.findViewById<TextView>(R.id.lecture_is_running)
        val day = item.findViewById<TextView>(R.id.lecture_day)
        val courseName_l = item.findViewById<TextView>(R.id.subject_name_tv)
        val location_l = item.findViewById<TextView>(R.id.place_id_tv)
        val lecturer = item.findViewById<TextView>(R.id.prof_name_tv)

        val from_l = item.findViewById<TextView>(R.id.from_time_tv)
        val to_l = item.findViewById<TextView>(R.id.to_time_tv)
        val attend_l = item.findViewById<CardView>(R.id.attend_card_lecture)

        val recyItem = item.findViewById<ConstraintLayout>(R.id.lecture_view)

        init {

            attend_l.setOnClickListener {
                onAttendClicked.invoke( dataList[adapterPosition])
            }
            recyItem.setOnClickListener{
                onItemClick.invoke(dataList[adapterPosition])
            }

        }
    }

}


