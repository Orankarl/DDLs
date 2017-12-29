package com.example.orankarl.ddls;

import org.litepal.crud.DataSupport;

import java.util.Calendar;

/**
 * Created by orankarl on 2017/12/28.
 */

public class Deadline extends DataSupport {
    private long id;
    private long calendarMillis;
    private String title;
    private String info;
    private String userName;

    public Deadline(long calendarMillis, String title, String info, String userName) {
        this.calendarMillis = calendarMillis;
        this.title = title;
        this.info = info;
        this.userName = userName;
    }

    public Deadline(Deadline deadline) {
        this.id = deadline.getId();
        this.calendarMillis = deadline.getCalendarMillis();
        this.title = deadline.getTitle();
        this.info = deadline.getInfo();
        this.userName = deadline.getUserName();
    }

    public long getId() {
        return id;
    }

    public long getCalendarMillis() {
        return calendarMillis;
    }

    public void setCalendarMillis(long calendarMillis) {
        this.calendarMillis = calendarMillis;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
