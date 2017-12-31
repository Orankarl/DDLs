package com.example.orankarl.ddls;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by orankarl on 2017/12/28.
 */
@Table("tb_user")
public class User{
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;
    @NotNull
    @Column("username")
    private String username;
//    private List<Deadline> deadlineList = new ArrayList<Deadline>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }


    //    public List<Deadline> getDeadlineList() {
//        return deadlineList;
//    }

//    public void setDeadlineList(List<Deadline> deadlineList) {
//        this.deadlineList = deadlineList;
//    }
}
