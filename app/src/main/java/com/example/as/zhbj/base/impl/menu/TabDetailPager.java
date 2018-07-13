package com.example.as.zhbj.base.impl.menu;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.as.zhbj.base.BaseMenuDetailPager;

/**
 * 页签页面对象
 */
public class TabDetailPager extends BaseMenuDetailPager {
    public TabDetailPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        //给帧布局填充对象
        TextView view = new TextView(mActivity);
        view.setText("页签");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
    }
}
