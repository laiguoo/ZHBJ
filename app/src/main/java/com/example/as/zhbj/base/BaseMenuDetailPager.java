package com.example.as.zhbj.base;

import android.app.Activity;
import android.view.View;

/**
 * 菜单详情页基类
 */
public abstract class BaseMenuDetailPager {

    public Activity mActivity;
    public View mRootView;    //菜单详情页根布局

    public BaseMenuDetailPager(Activity activity) {
        mActivity = activity;
        mRootView = initView();
    }

    /**
     * 初始化布局必需子类去实现
     *
     * @return
     */
    public abstract View initView();

    public void initData() {

    }
}
