package com.example.orankarl.ddls

import android.util.Log
import com.example.orankarl.ddls.R.drawable.user
import org.litepal.crud.DataSupport
import java.util.*

/**
 * Created by orankarl on 2017/12/29.
 */
class FinishedDeadlineList {
    private var finishedDeadlineList:MutableList<FinishedDeadline> = mutableListOf()

    companion object {
        fun deleteFinishedDeadline(id:Long):Int {
            return DataSupport.delete(FinishedDeadline::class.java, id)
        }
        fun queryDeadline(id:Long):FinishedDeadline {
            return DataSupport.find(FinishedDeadline::class.java, id)
        }
    }

    fun updateDeadlineList() {

    }

    fun add(calendarMillis:Long, title:String, info:String, user:User, finishedMillis:Long):Int {
        val finishedDeadline = FinishedDeadline(calendarMillis, title, info, user, finishedMillis)
        finishedDeadline.save()
        return 0
    }

    fun loadFinishedDeadlineList(username:String) {
        finishedDeadlineList.clear()
        finishedDeadlineList = DataSupport.where("userName = ?", username).find(FinishedDeadline::class.java)
        Collections.sort(finishedDeadlineList, FinishedDeadlineComparator.INSTANCE)
    }

    fun get(position:Int):FinishedDeadline {
        return finishedDeadlineList[position]
    }

    fun size():Int {
        return finishedDeadlineList.size
    }
}