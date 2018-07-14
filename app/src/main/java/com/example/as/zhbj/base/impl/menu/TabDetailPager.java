package com.example.as.zhbj.base.impl.menu;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.as.zhbj.R;
import com.example.as.zhbj.base.BaseMenuDetailPager;
import com.example.as.zhbj.domain.NewsMenu;
import com.example.as.zhbj.domain.NewsTabBean;
import com.example.as.zhbj.global.GlobalConstants;
import com.example.as.zhbj.utils.CacheUtils;
import com.example.as.zhbj.view.TopNewsViewPager;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 页签页面对象
 */
public class TabDetailPager extends BaseMenuDetailPager {
    private NewsMenu.NewsTabData mTabData;  //单个页签的网络数据
    private TopNewsViewPager mViewPager;
    private final String mUrl;
    private ArrayList<NewsTabBean.TopNews> mTopNews;
    //    private TextView view;

    public TabDetailPager(Activity activity, NewsMenu.NewsTabData newsTabData) {
        super(activity);
        mTabData = newsTabData;
        mUrl = GlobalConstants.SERVER_URL + mTabData.url;
    }

    @Override
    public View initView() {
        //给帧布局填充对象
//        view = new TextView(mActivity);
////        view.setText(mTabData.title);   //此处空指针
//        view.setTextColor(Color.RED);
//        view.setTextSize(22);
//        view.setGravity(Gravity.CENTER);

        View view = View.inflate(mActivity, R.layout.pager_tab_detail, null);
        mViewPager = view.findViewById(R.id.vp_top_news);
        return view;
    }

    @Override
    public void initData() {
        String cache = CacheUtils.getCache(mActivity, mUrl);
        if (!TextUtils.isEmpty(cache)) {
            processData(cache);
        }
//        view.setText(mTabData.title);
        getDataFromServer();

    }

    private void getDataFromServer() {
        RequestParams requestParams = new RequestParams(mUrl);

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processData(result);
                CacheUtils.setCache(mActivity, mUrl, result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                Toast.makeText(mActivity, "请求失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void processData(String result) {
        Gson gson = new Gson();
        NewsTabBean newsTabBean = gson.fromJson(result, NewsTabBean.class);
        mTopNews = newsTabBean.data.topnews;

        if (mTopNews != null) {
            mViewPager.setAdapter(new TopNewsAdapter());
        }
    }

    class TopNewsAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mTopNews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(mActivity);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY); //设置图片缩放比

            imageView.setImageResource(R.drawable.topnews_item_default);

            //下载图片,将图片设置给ImageView，---避免内存溢出，--- 缓存
            //BitmapUtils--xUtils
            x.image().bind(imageView, GlobalConstants.SERVER_URL + mTopNews.get(position).topimage);

            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
