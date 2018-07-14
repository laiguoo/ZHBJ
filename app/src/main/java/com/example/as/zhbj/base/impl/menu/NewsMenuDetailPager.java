package com.example.as.zhbj.base.impl.menu;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.as.zhbj.MainActivity;
import com.example.as.zhbj.R;
import com.example.as.zhbj.base.BaseMenuDetailPager;
import com.example.as.zhbj.domain.NewsMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 菜单详情页--新闻
 * <p>
 * ViewPagerIndicator的使用流程
 * <p>
 * 1.引入库
 * 2.解决support-v4冲突
 * 3.从例子中拷贝布局文件
 * 4.从例子中烤贝相关代码
 * 5.在清单文件中配置样式
 * 6.修改背景
 * 7.修改样式--背景、文字样式
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager implements ViewPager.OnPageChangeListener {

    private static final String TAG = "NewsMenuDetailPager";

    private ViewPager mViewPager;
    private ArrayList<NewsMenu.NewsTabData> mTabData;
    private ArrayList<TabDetailPager> mPagers; //页签页面集合
    private TabPageIndicator mIndicator;
    private ImageButton mNextPage;

    public NewsMenuDetailPager(Activity activity, ArrayList<NewsMenu.NewsTabData> children) {
        super(activity);

        mTabData = children;
    }

    @Override
    public View initView() {
        //给帧布局填充对象
        View view = View.inflate(mActivity, R.layout.pager_news_menu_detail, null);
        mViewPager = view.findViewById(R.id.vp_news_menu_detail);
        mIndicator = view.findViewById(R.id.indicator);
        mNextPage = view.findViewById(R.id.btn_next);

        return view;
    }

    @Override
    public void initData() {
        //初始化页签
        mPagers = new ArrayList<>();
        for (int i = 0; i < mTabData.size(); i++) {
            TabDetailPager tabDetailPager = new TabDetailPager(mActivity, mTabData.get(i));
            mPagers.add(tabDetailPager);
        }
        mViewPager.setAdapter(new NewsMenuDetailAdapter());

        mIndicator.setViewPager(mViewPager);    //将ViewPager和指示器绑定在一起,必须在ViewPager设置完数据之后

        //设置页面滑动监听
//        mViewPager.addOnPageChangeListener(this);
        //给指示器设置页面监听
        mIndicator.setOnPageChangeListener(this);

        mNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentItem = mViewPager.getCurrentItem();
                currentItem++;
                mViewPager.setCurrentItem(currentItem);
            }
        });

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.i(TAG, "当前位置..... " + position);
        if (position == 0) {
            //开启侧边栏
            setSlidingMenuEnable(true);
        } else {
            //关闭侧边栏
            setSlidingMenuEnable(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 开启或禁用侧边栏
     *
     * @param enable
     */
    private void setSlidingMenuEnable(boolean enable) {
        //获取侧边栏对象
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        if (enable) {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }

    }

    /*public void nextPage(View v) {
        //调到下一个页面
        int currentItem = mViewPager.getCurrentItem();
        currentItem++;
        mViewPager.setCurrentItem(currentItem);

    }*/

    class NewsMenuDetailAdapter extends PagerAdapter {

        //指定指示器的标题
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            NewsMenu.NewsTabData newsTabData = mTabData.get(position);

            return newsTabData.title;
        }

        @Override
        public int getCount() {
            return mPagers.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TabDetailPager tabDetailPager = mPagers.get(position);
            container.addView(tabDetailPager.mRootView);
            tabDetailPager.initData();
            View mRootView = tabDetailPager.mRootView;


            return mRootView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);

        }
    }
}
