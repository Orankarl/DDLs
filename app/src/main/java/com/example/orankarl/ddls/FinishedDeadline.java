package com.example.orankarl.ddls;

import org.litepal.crud.DataSupport;

/**
 * Created by orankarl on 2017/12/29.
 */

public class FinishedDeadline extends DataSupport {
    private long id;
    private long calendarMillis;
    private long finishedMillis;
    private String title;
    private String info;
    private String userName;

    public FinishedDeadline(long calendarMillis, String title, String info, User user, long finishedMillis) {
        this.calendarMillis = calendarMillis;
        this.title = title;
        this.info = info;
        this.userName = user.getUsername();
        this.finishedMillis = finishedMillis;
    }

    public FinishedDeadline(FinishedDeadline finishedDeadline) {
        this.calendarMillis = finishedDeadline.calendarMillis;
        this.title = finishedDeadline.title;
        this.info = finishedDeadline.info;
        this.userName = finishedDeadline.userName;
        this.finishedMillis = finishedDeadline.finishedMillis;
    }

    public FinishedDeadline(Deadline deadline, Long finishedMillis) {
        this.calendarMillis = deadline.getCalendarMillis();
        this.title = deadline.getTitle();
        this.info = deadline.getInfo();
        this.userName = deadline.getUserName();
        this.finishedMillis = finishedMillis;
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

    public long getFinishedMillis() {
        return finishedMillis;
    }

    public void setFinishedMillis(long finishedMillis) {
        this.finishedMillis = finishedMillis;
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
