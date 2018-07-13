package com.example.as.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.example.as.zhbj.base.BasePager;

/**
 * 政务
 */
public class GovAffairsPager extends BasePager {
    private static final String TAG = "HomePager";

    public GovAffairsPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        Log.i(TAG, "政务初始化了.....");
        //给帧布局填充对象
        TextView textView = new TextView(mActivity);
        textView.setText("政务");
        textView.setTextColor(Color.RED);
        textView.setTextSize(22);
        textView.setGravity(Gravity.CENTER);

        flContent.addView(textView);

        //修改标题
        tvTitle.setText("人口管理");
    }
}

