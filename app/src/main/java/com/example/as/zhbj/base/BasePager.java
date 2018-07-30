package com.example.as.zhbj.base;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.as.zhbj.MainActivity;
import com.example.as.zhbj.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * 五个标签页的基本类
 */
public class BasePager {

    public   Activity mActivity;

    public TextView tvTitle;
    public ImageButton btnMennu;
    public FrameLayout flContent;   //空的帧布局对象，要动态添加布局
    public View mRootView;    //当前页面的布局对象
    public ImageButton btnPhoto;

    public BasePager(Activity activity) {
        this.mActivity = activity;
        mRootView = initView();
    }

    //初始化布局
    public View initView() {
        View view = View.inflate(mActivity, R.layout.base_pager, null);
        tvTitle = view.findViewById(R.id.tv_title);
        btnMennu = view.findViewById(R.id.btn_menu);
        flContent = view.findViewById(R.id.fl_content);
        btnPhoto = view.findViewById(R.id.btn_photo);
        btnMennu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        return view;
    }

    /**
     * 打开或者关闭侧边栏
     */
    private void toggle() {
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        slidingMenu.toggle();
    }

    //初始化数据
    public void initData() {

    }
}
