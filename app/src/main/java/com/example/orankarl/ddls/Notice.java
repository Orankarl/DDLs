package com.example.orankarl.ddls;

import org.litepal.crud.DataSupport;

/**
 * Created by orankarl on 2017/12/29.
 */

public class Notice extends DataSupport {
    private Long id;
    private Long calendarMillis;
    private String title;
    private String from;
    private String info;
    private String userName;

    public Notice(Long calendarMillis, String title, String from, String info, User user) {
        this.calendarMillis = calendarMillis;
        this.title = title;
        this.from = from;
        this.info = info;
        this.userName = user.getUsername();
    }

    public Long getId() {
        return id;
    }

    public Long getCalendarMillis() {
        return calendarMillis;
    }

    public void setCalendarMillis(Long calendarMillis) {
        this.calendarMillis = calendarMillis;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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
