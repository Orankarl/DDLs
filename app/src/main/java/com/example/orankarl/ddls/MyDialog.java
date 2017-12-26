package com.example.orankarl.ddls;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;

/**
 * Created by orankarl on 2017/12/26.
 */

public class MyDialog extends Dialog {
    public MyDialog(Context text) {
        super(text, R.style.FullScreenDialogStyle);
        setContentView(R.layout.add_deadline_dialog);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }
}
