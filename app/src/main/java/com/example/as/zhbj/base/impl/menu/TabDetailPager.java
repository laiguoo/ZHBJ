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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.as.zhbj.R;
import com.example.as.zhbj.base.BaseMenuDetailPager;
import com.example.as.zhbj.domain.NewsMenu;
import com.example.as.zhbj.domain.NewsTabBean;
import com.example.as.zhbj.global.GlobalConstants;
import com.example.as.zhbj.utils.CacheUtils;
import com.example.as.zhbj.view.PullToRefreshListView;
import com.example.as.zhbj.view.TopNewsViewPager;
import com.google.gson.Gson;
import com.viewpagerindicator.CirclePageIndicator;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
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
    private TextView tvTitle;
    private CirclePageIndicator mIndicator;
    private PullToRefreshListView lvList;
    private ArrayList<NewsTabBean.NewsData> mNewsList;
    private NewsAdapter mNewsAdapter;
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
        /*mViewPager = view.findViewById(R.id.vp_top_news);
        tvTitle = view.findViewById(R.id.tv_title);
        mIndicator = view.findViewById(R.id.indicator);*/
        lvList = view.findViewById(R.id.lv_list);

        //给ListView添加头布局
        View mHeaderView = View.inflate(mActivity, R.layout.list_item_header, null);
        lvList.addHeaderView(mHeaderView);

        //5.设置回调
        lvList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新数据
                getDataFromServer();
            }
        });

        mViewPager = mHeaderView.findViewById(R.id.vp_top_news);
        tvTitle = mHeaderView.findViewById(R.id.tv_title);
        mIndicator = mHeaderView.findViewById(R.id.indicator);

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

                //下拉收起控件
                lvList.onRefreshCompleted(true);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                Toast.makeText(mActivity, "请求失败", Toast.LENGTH_SHORT).show();

                //下拉收起控件
                lvList.onRefreshCompleted(false);
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
        //头条新闻
        mTopNews = newsTabBean.data.topnews;

        if (mTopNews != null) {
            mViewPager.setAdapter(new TopNewsAdapter());

            mIndicator.setViewPager(mViewPager);
            mIndicator.setSnap(true);   //快照方式显示

            //事件要设置给indicator
            mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //更新头条新闻的标题
                    NewsTabBean.TopNews topNews = mTopNews.get(position);
                    tvTitle.setText(topNews.title);

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            //更新第一个新闻头条标题
            tvTitle.setText(mTopNews.get(0).title);

            mIndicator.onPageSelected(0);   //默认让第一个选中（解决页面销毁后重新初始化时，Indicator仍保留上次远点的bug）

        }

        //列表新闻
        mNewsList = newsTabBean.data.news;
        if (mNewsList != null) {
            mNewsAdapter = new NewsAdapter();
            lvList.setAdapter(mNewsAdapter);

        }
    }

    class TopNewsAdapter extends PagerAdapter {

        private final ImageOptions mImageOptions;

        public TopNewsAdapter() {
            ImageOptions.Builder builder = new ImageOptions.Builder();
            builder.setFadeIn(true);
            builder.setLoadingDrawableId(R.drawable.topnews_item_default);  //加载中的默认图片
            mImageOptions = builder.build();
        }

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

//            imageView.setImageResource(R.drawable.topnews_item_default);

            //下载图片,将图片设置给ImageView，---避免内存溢出，--- 缓存
            //BitmapUtils--xUtils
            x.image().bind(imageView, GlobalConstants.SERVER_URL + mTopNews.get(position).topimage, mImageOptions);

            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    class NewsAdapter extends BaseAdapter {

        private final ImageOptions mImageOptions;

        public NewsAdapter() {
            ImageOptions.Builder builder = new ImageOptions.Builder();
            builder.setLoadingDrawableId(R.drawable.news_pic_default);
            mImageOptions = builder.build();
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public NewsTabBean.NewsData getItem(int position) {
            return mNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.list_item_news, null);
                holder = new ViewHolder();
                holder.ivIcon = convertView.findViewById(R.id.iv_icon);
                holder.tvTitle = convertView.findViewById(R.id.tv_title);
                holder.tvData = convertView.findViewById(R.id.tv_date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            NewsTabBean.NewsData news = getItem(position);
            holder.tvTitle.setText(news.title);
            holder.tvData.setText(news.pubdate);
            x.image().bind(holder.ivIcon, GlobalConstants.SERVER_URL + news.listimage, mImageOptions);
            return convertView;
        }
    }

    static class ViewHolder {
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvData;
    }
}
