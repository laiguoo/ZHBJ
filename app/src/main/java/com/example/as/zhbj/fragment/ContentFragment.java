package com.example.as.zhbj.fragment;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.as.zhbj.MainActivity;
import com.example.as.zhbj.R;
import com.example.as.zhbj.base.BasePager;
import com.example.as.zhbj.base.impl.GovAffairsPager;
import com.example.as.zhbj.base.impl.HomePager;
import com.example.as.zhbj.base.impl.NewsCenterPager;
import com.example.as.zhbj.base.impl.SettingPager;
import com.example.as.zhbj.base.impl.SmartServicePager;
import com.example.as.zhbj.view.NoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

/**
 * 主页面Fragment
 */
public class ContentFragment extends BaseFragment {

    private NoScrollViewPager mViewPager;
    private ArrayList<BasePager> mPagers;   //五个标签页的集合
    private RadioGroup rgGroup;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_content, null);
        mViewPager = view.findViewById(R.id.vp_content);
        rgGroup = view.findViewById(R.id.rg_group);
        return view;
    }

    @Override
    public void initData() {
        mPagers = new ArrayList<>();

        //添加五个标签页
        mPagers.add(new HomePager(mActivity));
        mPagers.add(new NewsCenterPager(mActivity));
        mPagers.add(new SmartServicePager(mActivity));
        mPagers.add(new GovAffairsPager(mActivity));
        mPagers.add(new SettingPager(mActivity));

        mViewPager.setAdapter(new ContentAdapter());

        //底栏标签切换监听
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        //首页
//                        mViewPager.setCurrentItem(0);
                        mViewPager.setCurrentItem(0, false); //参数二表示是否有滑动动画
                        break;
                    case R.id.rb_news:
                        mViewPager.setCurrentItem(1, false);
                        break;
                    case R.id.rb_smart:
                        mViewPager.setCurrentItem(2, false);
                        break;
                    case R.id.rb_gov:
                        mViewPager.setCurrentItem(3, false);
                        break;
                    case R.id.rb_setting:
                        mViewPager.setCurrentItem(4, false);
                        break;
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                BasePager basePager = mPagers.get(position);
                basePager.initData();
                if (position == 0 || position == mPagers.size() - 1) {
                    //首页和设置页要禁用侧边栏
                    setSlidingMenuEnable(false);
                } else {
                    //开启侧边栏
                    setSlidingMenuEnable(true);
                }
            }

            @Override
            public void onPageSelected(int position) {
                /*BasePager basePager = mPagers.get(position);
                basePager.initData();*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //手动加载第一页数据（在onPageScrolled初始化数据时不用在这里为第一次初始化数据）
//        mPagers.get(0).initData();

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

    class ContentAdapter extends PagerAdapter {
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
            BasePager basePager = mPagers.get(position);
            View view = basePager.mRootView;    //当前页面的布局

//            basePager.initData();   //初始化数据   为了节省流量和性能，不在此处初始化数据

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * 获取新闻中心的页面
     */
    public NewsCenterPager getNewsCenterPager() {
        return (NewsCenterPager) mPagers.get(1);
    }

}
