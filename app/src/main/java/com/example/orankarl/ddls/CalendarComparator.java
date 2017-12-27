package com.example.orankarl.ddls;

import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by orankarl on 2017/12/27.
 */

public class CalendarComparator implements Comparator {
    public int compare(Object x, Object y) {
        Calendar xcal =  ((Calendar) x);
        Calendar ycal = ((Calendar) y);
        if ( xcal.get(Calendar.YEAR) < ycal.get(Calendar.YEAR) ) return -1;
        else if (xcal.get(Calendar.YEAR) > ycal.get(Calendar.YEAR)) return 1;
        else {
            if ( xcal.get(Calendar.MONTH) < ycal.get(Calendar.MONTH) ) return -1;
            else if (  xcal.get(Calendar.MONTH) > ycal.get(Calendar.MONTH) ) return 1;
            else {
                if ( xcal.get(Calendar.DAY_OF_MONTH) < ycal.get(Calendar.DAY_OF_MONTH) ) return -1;
                else if ( xcal.get(Calendar.DAY_OF_MONTH) > ycal.get(Calendar.DAY_OF_MONTH) ) return 1;
                return 0;
            }
        }
    }

    public static final Comparator INSTANCE = new CalendarComparator();
}
