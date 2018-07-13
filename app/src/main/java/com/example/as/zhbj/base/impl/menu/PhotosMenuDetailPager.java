package com.example.as.zhbj.base.impl.menu;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.as.zhbj.base.BaseMenuDetailPager;

/**
 * 菜单详情页--组图
 */
public class PhotosMenuDetailPager extends BaseMenuDetailPager {

    public PhotosMenuDetailPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        //给帧布局填充对象
        TextView view = new TextView(mActivity);
        view.setText("菜单详情页--组图");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);
        return view;
    }
}
