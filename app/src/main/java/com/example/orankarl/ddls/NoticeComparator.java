package com.example.orankarl.ddls;

import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by orankarl on 2017/12/27.
 */

public class NoticeComparator implements Comparator {
    public int compare(Object x, Object y) {
        Long xmillis = ((Notice) x).getCalendarMillis();
        Long ymillis = ((Notice) y).getCalendarMillis();
        if (xmillis < ymillis) return -1;
        else if (xmillis > ymillis) return 1;
        return 0;
    }

    public static final Comparator INSTANCE = new NoticeComparator();
}
