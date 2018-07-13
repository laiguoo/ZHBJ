package com.example.as.zhbj.base.impl.menu;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.as.zhbj.R;
import com.example.as.zhbj.base.BaseMenuDetailPager;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 菜单详情页--新闻
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager {

    private ViewPager mViewPager;

    public NewsMenuDetailPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        //给帧布局填充对象
        View view = View.inflate(mActivity, R.layout.pager_news_menu_detail, null);
        mViewPager = view.findViewById(R.id.vp_news_menu_detail);
        return view;
    }

    @Override
    public void initData() {

    }

    class NewsMenuDetailAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);

        }
    }
}
