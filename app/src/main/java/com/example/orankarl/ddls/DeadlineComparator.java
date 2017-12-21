package com.example.orankarl.ddls;

import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by orankarl on 2017/12/22.
 */

public class DeadlineComparator implements Comparator {
    public int compare(Object x, Object y) {
        Calendar xcal = ((Deadline) x).getCalendar();
        Calendar ycal = ((Deadline) y).getCalendar();
        if ( xcal.before(ycal) ) return -1;
        if ( xcal.after(ycal) ) return 1;
        return 0;
    }

    public static final Comparator INSTANCE = new DeadlineComparator();

}
