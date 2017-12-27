package com.example.orankarl.ddls

import android.util.Log
import java.text.FieldPosition
import java.time.Year
import java.util.*

/**
 * Created by orankarl on 2017/12/21.
 */
class Deadline(var calendar: Calendar, var title:String, var info:String)

class DeadlineList() {
    private var deadLineList:MutableList<Deadline> = mutableListOf()

    init {
        deadLineList.clear()
        deadLineList.add(Deadline(getNewCalendar(2018, 2, 11), "组合数学作业", "第十三次"))
        deadLineList.add(Deadline(getNewCalendar(2017, 11, 10), "图形学大作业", "Unity Project. Working with A, B, C, D, E and F. Be responsible for OBing."))
        deadLineList.add(Deadline(getNewCalendar(2018, 1, 1), "数据库大作业", ""))
        deadLineList.add(Deadline(getNewCalendar(2018, 1, 1), "人工智能大作业", "Building neural network by C++ (Without using any existing package)."))
    }

    fun updateDeadlineList() {
        Collections.sort(deadLineList, DeadlineComparator.INSTANCE)
    }

    fun add(calendar: Calendar, title:String, info:String):Int {
        if (title == "") return 1
        val calendar1 = Calendar.getInstance()
        Log.d("present", calendar1.get(Calendar.YEAR).toString()
                +" "+calendar1.get(Calendar.MONTH).toString()
                +" "+calendar1.get(Calendar.DAY_OF_MONTH).toString())
        Log.d("item time", calendar.get(Calendar.YEAR).toString()
                +" "+calendar.get(Calendar.MONTH).toString()
                +" "+calendar.get(Calendar.DAY_OF_MONTH).toString())
        if (CalendarComparator.INSTANCE.compare(calendar1, calendar) == 1) return 2
        deadLineList.add(Deadline(calendar, title, info))
        return 0
    }
    fun loadDeadlineList() {
        Collections.sort(deadLineList, DeadlineComparator.INSTANCE)
    }

    fun get(position: Int):Deadline {
        return deadLineList[position]
    }

    fun size():Int {
        return deadLineList.size
    }

    private fun getNewCalendar(year:Int, month:Int, day:Int):Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month-1)
            set(Calendar.DAY_OF_MONTH, day)
        }
    }

    fun isEmpty():Boolean {
        return deadLineList.isEmpty()
    }
}