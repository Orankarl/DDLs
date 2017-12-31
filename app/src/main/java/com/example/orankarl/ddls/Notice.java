package com.example.orankarl.ddls;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import org.litepal.crud.DataSupport;

/**
 * Created by orankarl on 2017/12/29.
 */
@Table("tb_notice")
public class Notice {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private Long id;
    @NotNull
    private Long calendarMillis;
    @NotNull
    private String title;
    private String creator;
    private String info;
    @Column("username")
    private String userName;

    public Notice(Long calendarMillis, String title, String from, String info, String username) {
        this.calendarMillis = calendarMillis;
        this.title = title;
        this.creator = from;
        this.info = info;
        this.userName = username;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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
