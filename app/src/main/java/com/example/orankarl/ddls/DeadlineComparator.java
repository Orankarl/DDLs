package com.example.orankarl.ddls;

import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by orankarl on 2017/12/22.
 */

public class DeadlineComparator implements Comparator {
    public int compare(Object x, Object y) {
//        Calendar xcal = Calendar.getInstance();
//        xcal.setTimeInMillis(((Deadline) x).getCalendarMillis());
//        Calendar ycal = Calendar.getInstance();
//        xcal.setTimeInMillis(((Deadline) y).getCalendarMillis());
//        if ( xcal.get(Calendar.YEAR) < ycal.get(Calendar.YEAR) ) return 1;
//        else if (xcal.get(Calendar.YEAR) > ycal.get(Calendar.YEAR)) return -1;
//        else {
//            if ( xcal.get(Calendar.MONTH) < ycal.get(Calendar.MONTH) ) return 1;
//            else if (  xcal.get(Calendar.MONTH) > ycal.get(Calendar.MONTH) ) return -1;
//            else {
//                if ( xcal.get(Calendar.DAY_OF_MONTH) < ycal.get(Calendar.DAY_OF_MONTH) ) return 1;
//                else if ( xcal.get(Calendar.DAY_OF_MONTH) > ycal.get(Calendar.DAY_OF_MONTH) ) return -1;
//                return 0;
//            }
//        }
        if (((Deadline) x).getCalendarMillis() < ((Deadline) y).getCalendarMillis()) return -1;
        else if (((Deadline) x).getCalendarMillis() > ((Deadline) y).getCalendarMillis()) return 1;
        return 0;
    }

    public static final Comparator INSTANCE = new DeadlineComparator();

}
