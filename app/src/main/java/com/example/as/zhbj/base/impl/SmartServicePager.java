package com.example.as.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.example.as.zhbj.base.BasePager;

/**
 *智慧服务
 */
public class SmartServicePager extends BasePager {
    private static final String TAG = "HomePager";

    public SmartServicePager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        Log.i(TAG, "智慧服务初始化了.....");
        //给帧布局填充对象
        TextView textView = new TextView(mActivity);
        textView.setText("智慧服务");
        textView.setTextColor(Color.RED);
        textView.setTextSize(22);
        textView.setGravity(Gravity.CENTER);

        flContent.addView(textView);

        //修改标题
        tvTitle.setText("生活");
    }
}

