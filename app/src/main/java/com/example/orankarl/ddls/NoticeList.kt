package com.example.orankarl.ddls

import android.util.Log
import org.litepal.crud.DataSupport
import java.util.*

/**
 * Created by orankarl on 2017/12/27.
 */

class NoticeList() {
    private var noticeList:MutableList<Notice> = mutableListOf()

    init {
        noticeList.clear()
//        noticeList.add(Notice(getNewCalendar(2017, 10, 11), "期中考通知", "组合数学", "时间：xxx\n地点：公教楼xxx课室"))
//        noticeList.add(Notice(getNewCalendar(2017, 12, 26), "期末展示通知", "数据库系统原理", "1月3号下午在教室进行，请所有小组务必准备好展示用材料"))
//        noticeList.add(Notice(getNewCalendar(2017, 11, 14), "大作业通知", "移动互联网编程实践", "五人一组\n作业要求见课程主页\n截止日期12.24"))
//        noticeList.add(Notice(getNewCalendar(2017, 12, 25), "作业通知", "数值计算", "P535:1(b),2"))
//        noticeList.add(Notice(getNewCalendar(2017, 12, 21), "作业通知", "组合数学与数论", "第十四次作业，12.28上课时交"))
    }

    fun loadNoticeList(user:User) {
        noticeList.clear()
        noticeList = DataSupport.where("userName = ?", user.username).find(Notice::class.java)
        Collections.sort(noticeList, NoticeComparator.INSTANCE)
    }

    fun get(position:Int):Notice {
        return noticeList[position]
    }

    private fun getNewCalendar(year:Int, month:Int, day:Int):Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month-1)
            set(Calendar.DAY_OF_MONTH, day)
        }
    }

    fun size():Int {
        return noticeList.size
    }

    fun isEmpty():Boolean {
        return noticeList.isEmpty()
    }
}