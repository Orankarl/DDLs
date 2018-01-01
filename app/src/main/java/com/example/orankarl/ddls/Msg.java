package com.example.orankarl.ddls;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.sql.Time;

/**
 * Created by orankarl on 2017/12/31.
 */

@Table("tb_msg")
public class Msg {
    public static int LEFT = 0, RIGHT = 1;
//    public static enum Msg_Type{TYPE_SEND, TYPE_RECEIVE}
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private long id;
    private long time;
    @Column("course_id")
    @NotNull
    private long course_id;
    @Column("username")
    @NotNull
    private String username;
    private String sender;
    private String content;
    private int type;

    Msg(long time, long course_id, String username, String sender, String content, int type) {
        this.time = time;
        this.course_id = course_id;
        this.username = username;
        this.sender = sender;
        this.content = content;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getCourse_id() {
        return course_id;
    }

    public void setCourse_id(long course_id) {
        this.course_id = course_id;
    }
}
