package com.example.orankarl.ddls;

import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by orankarl on 2017/12/27.
 */

public class NoticeComparator implements Comparator {
    public int compare(Object x, Object y) {
        int xId = ((Notice) x).getId();
        int yId = ((Notice) y).getId();
        if (xId < yId) return -1;
        else if (xId > yId) return 1;
        return 0;
    }

    public static final Comparator INSTANCE = new NoticeComparator();
}
