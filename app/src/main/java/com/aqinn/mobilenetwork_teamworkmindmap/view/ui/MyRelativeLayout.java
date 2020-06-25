package com.aqinn.mobilenetwork_teamworkmindmap.view.ui;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @author Aqinn
 * @date 2020/6/25 6:32 PM
 */
public class MyRelativeLayout extends RelativeLayout implements View.OnTouchListener {


    public MyRelativeLayout(Context context) {
        super(context);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return true;
    }

}
