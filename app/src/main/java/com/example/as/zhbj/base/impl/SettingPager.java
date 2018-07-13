package com.example.as.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.as.zhbj.base.BasePager;

/**
 * 设置
 */
public class SettingPager extends BasePager {
    private static final String TAG = "HomePager";

    public SettingPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        Log.i(TAG, "设置初始化了.....");
        //给帧布局填充对象
        TextView textView = new TextView(mActivity);
        textView.setText("设置");
        textView.setTextColor(Color.RED);
        textView.setTextSize(22);
        textView.setGravity(Gravity.CENTER);

        flContent.addView(textView);

        //修改标题
        tvTitle.setText("设置");
        btnMennu.setVisibility(View.GONE);
    }
}

