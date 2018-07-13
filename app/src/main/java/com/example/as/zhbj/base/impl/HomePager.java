package com.example.as.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.as.zhbj.base.BasePager;

/**
 * 首页
 */
public class HomePager extends BasePager {
    private static final String TAG = "HomePager";

    public HomePager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        Log.i(TAG, "首页初始化了.....");
        //给帧布局填充对象
        TextView textView = new TextView(mActivity);
        textView.setText("首页");
        textView.setTextColor(Color.RED);
        textView.setTextSize(22);
        textView.setGravity(Gravity.CENTER);

        flContent.addView(textView);

        //修改标题
        tvTitle.setText("智慧北京");
        btnMennu.setVisibility(View.GONE);
    }
}

