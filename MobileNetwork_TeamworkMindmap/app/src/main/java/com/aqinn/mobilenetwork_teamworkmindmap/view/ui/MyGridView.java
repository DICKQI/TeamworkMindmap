package com.aqinn.mobilenetwork_teamworkmindmap.view.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author Aqinn
 * @date 2020/6/14 11:18 PM
 */
public class MyGridView extends GridView {
    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView(Context context) {
        super(context);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //网上找的代码实现原理未研究透
    //使GridView不会出现滚动条，ScrollView嵌套ListView也是同样的道理
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
