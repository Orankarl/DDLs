package com.example.orankarl.ddls;

import java.util.Comparator;

/**
 * Created by orankarl on 2017/12/29.
 */

public class FinishedDeadlineComparator implements Comparator{
    public int compare(Object x, Object y) {
        if (((Deadline) x).getFinishedMillis() < ((Deadline) y).getFinishedMillis()) return -1;
        else if (((Deadline) x).getFinishedMillis() > ((Deadline) y).getFinishedMillis()) return 1;
        return 0;
    }

    public static final Comparator INSTANCE = new FinishedDeadlineComparator();
}
