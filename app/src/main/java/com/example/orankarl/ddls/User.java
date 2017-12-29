package com.example.orankarl.ddls;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by orankarl on 2017/12/28.
 */

public class User extends DataSupport {
    private String username;
    private List<Deadline> deadlineList = new ArrayList<Deadline>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Deadline> getDeadlineList() {
        return deadlineList;
    }

    public void setDeadlineList(List<Deadline> deadlineList) {
        this.deadlineList = deadlineList;
    }
}
