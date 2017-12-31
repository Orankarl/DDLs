package com.example.orankarl.ddls;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by orankarl on 2017/12/31.
 */

@Table("tb_msg")
public class Msg {
    public static enum Msg_Type{TYPE_SEND, TYPE_RECEIVE}
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private long id;
    private long time;
    private String username;
    private String content;
    private Msg_Type type;

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

    public Msg_Type getType() {
        return type;
    }

    public void setType(Msg_Type type) {
        this.type = type;
    }
}
