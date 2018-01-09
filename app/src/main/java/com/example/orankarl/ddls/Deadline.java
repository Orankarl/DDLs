package com.example.orankarl.ddls;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Default;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import org.litepal.crud.DataSupport;

import java.util.Calendar;

import kotlin.Function;

/**
 * Created by orankarl on 2017/12/28.
 */
@Table("tb_deadline")
public class Deadline {
    @PrimaryKey(AssignType.BY_MYSELF)
    private int id;
    @NotNull
    private long calendarMillis;
    @Column("finishedMillis")
    private long finishedMillis;
    @NotNull
    private String title;
    private String info;
    @Column("username")
    private String userName;
    @Column("finished")
    @Default("false")
    private boolean finished;

    public Deadline(int id, long calendarMillis, String title, String info, String userName, boolean done) {
        this.id = id;
        this.calendarMillis = calendarMillis;
        this.title = title;
        this.info = info;
        this.userName = userName;
        finished = done;
    }

    public Deadline(Deadline deadline) {
        this.id = deadline.getId();
        this.calendarMillis = deadline.getCalendarMillis();
        this.title = deadline.getTitle();
        this.info = deadline.getInfo();
        this.userName = deadline.getUserName();
    }

//    public Deadline(FinishedDeadline finishedDeadline) {
//        this.id = finishedDeadline.getId();
//        this.calendarMillis = finishedDeadline.getCalendarMillis();
//        this.title = finishedDeadline.getTitle();
//        this.info = finishedDeadline.getInfo();
//        this.userName = finishedDeadline.getUserName();
//    }

    public long getFinishedMillis() {
        return finishedMillis;
    }

    public void setFinishedMillis(long finishedMillis) {
        this.finishedMillis = finishedMillis;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
