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
    @PrimaryKey(AssignType.BY_MYSELF)
    private int id;
    @NotNull
    private String title;
    private String creator;
    private String info;
    @Column("username")
    private String userName;

    public Notice(int id, String title, String from, String info, String username) {
        this.id = id;
        this.title = title;
        this.creator = from;
        this.info = info;
        this.userName = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
