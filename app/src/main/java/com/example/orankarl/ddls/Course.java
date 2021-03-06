package com.example.orankarl.ddls;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by orankarl on 2017/12/31.
 */
@Table("tb_course")
public class Course {
    @PrimaryKey(AssignType.BY_MYSELF)
    private int id;
    private String title;
    private long user_id;
    private String username;
    private long time;
    private String latestName;
    private String latestMsg;
    private String semester;

    Course(int id, String title, String username, long time, String latestName, String latestMsg, String semester) {
        this.id = id;
        this.time = time;
        this.title = title;
        this.username = username;
        this.latestMsg = latestMsg;
        this.latestName = latestName;
        this.semester = semester;
    }

    Course(int id, String title, String semester, String username) {
        this.id = id;
        this.title = title;
        this.semester = semester;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLatestName() {
        return latestName;
    }

    public void setLatestName(String latestName) {
        this.latestName = latestName;
    }

    public String getLatestMsg() {
        return latestMsg;
    }

    public void setLatestMsg(String latestMsg) {
        this.latestMsg = latestMsg;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
