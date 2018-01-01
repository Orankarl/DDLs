package com.example.orankarl.ddls;

import java.util.Comparator;

/**
 * Created by orankarl on 2018/1/1.
 */

public class ChatComparator implements Comparator{
    public int compare(Object x, Object y) {
        Long xmillis = ((Course) x).getTime();
        Long ymillis = ((Course) y).getTime();
        if (xmillis < ymillis) return -1;
        else if (xmillis > ymillis) return 1;
        return 0;
    }

    public static final Comparator INSTANCE = new ChatComparator();
}
