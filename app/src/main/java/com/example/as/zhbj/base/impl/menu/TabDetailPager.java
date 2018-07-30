package com.example.as.zhbj.base.impl.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.as.zhbj.NewsDetailActivity;
import com.example.as.zhbj.R;
import com.example.as.zhbj.base.BaseMenuDetailPager;
import com.example.as.zhbj.domain.NewsMenu;
import com.example.as.zhbj.domain.NewsTabBean;
import com.example.as.zhbj.global.GlobalConstants;
import com.example.as.zhbj.utils.CacheUtils;
import com.example.as.zhbj.utils.PrefUtils;
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

    private static final String TAG = "TabDetailPager";

    private NewsMenu.NewsTabData mTabData;  //单个页签的网络数据
    private TopNewsViewPager mViewPager;
    private final String mUrl;
    private ArrayList<NewsTabBean.TopNews> mTopNews;
    private TextView tvTitle;
    private CirclePageIndicator mIndicator;
    private PullToRefreshListView lvList;
    private ArrayList<NewsTabBean.NewsData> mNewsList;
    private NewsAdapter mNewsAdapter;
    private String mMoreUrl;    //下一页数据连接
    //    private TextView view;

    private Handler mHandler;

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

            @Override
            public void onLoadMore() {
                //判断是否有下一页
                if (mMoreUrl != null) {
                    //有下一页
                    getMoreDataFromServer();
                } else {
                    //没有下一页
                    Toast.makeText(mActivity, "没有更多数据了", Toast.LENGTH_SHORT).show();
                    //没有数据时也要收起控件
                    lvList.onRefreshCompleted(true);
                }
            }
        });

        mViewPager = mHeaderView.findViewById(R.id.vp_top_news);
        tvTitle = mHeaderView.findViewById(R.id.tv_title);
        mIndicator = mHeaderView.findViewById(R.id.indicator);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerViewsCount = lvList.getHeaderViewsCount();
                position = position - headerViewsCount; //需要减去头布局的占位
                Log.i(TAG, "第" + position + "个被点击了");

                NewsTabBean.NewsData news = mNewsList.get(position);

                //read_ids： 1101,1102,1103
                String readIds = PrefUtils.getString(mActivity, "read_ids", "");
                if (!readIds.contains(news.id + "")) {  //避免重复添加
                    readIds = readIds + news.id + ",";
                    PrefUtils.setString(mActivity, "read_ids", readIds);
                }

                //要将被点击的item文字改为灰色,局部刷新
                TextView tvTitle = view.findViewById(R.id.tv_title);
                tvTitle.setTextColor(Color.GRAY);
//                mNewsAdapter.notifyDataSetChanged();    //全局刷新,浪费性能
                Intent intent = new Intent(mActivity, NewsDetailActivity.class);
                intent.putExtra("url", news.url);
                mActivity.startActivity(intent);

            }
        });

        return view;
    }

    /**
     * 加载下一页数据
     */
    private void getMoreDataFromServer() {
        RequestParams requestParams = new RequestParams(mMoreUrl);

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processData(result, true);

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

    @Override
    public void initData() {

        String cache = CacheUtils.getCache(mActivity, mUrl);
        if (!TextUtils.isEmpty(cache)) {
            processData(cache, false);
        }
//        view.setText(mTabData.title);
        getDataFromServer();

    }

    private void getDataFromServer() {
        RequestParams requestParams = new RequestParams(mUrl);

        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processData(result, false);
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

    @SuppressLint({"HandlerLeak", "ClickableViewAccessibility"})
    private void processData(String result, boolean isMore) {
        Gson gson = new Gson();
        NewsTabBean newsTabBean = gson.fromJson(result, NewsTabBean.class);

        String more = newsTabBean.data.more;
        if (!TextUtils.isEmpty(more)) {
            mMoreUrl = GlobalConstants.SERVER_URL + more;
        } else {
            mMoreUrl = null;
        }
        if (!isMore) {
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
            if (mHandler == null) {
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        int currentItem = mViewPager.getCurrentItem();
                        currentItem++;
                        if (currentItem > mTopNews.size() - 1) {
                            currentItem = 0;
                        }
                        mViewPager.setCurrentItem(currentItem);
                        mHandler.sendEmptyMessageDelayed(0, 3000);//继续发送延时3秒的消息

                    }
                };

                mHandler.sendEmptyMessageDelayed(0, 3000);//发送延时3秒的消息

                mViewPager.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch(event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                //停止广告自动轮播
                                //删除handler的所有消息
                                mHandler.removeCallbacksAndMessages(null);
                               /* mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //在主线程中运行
                                    }
                                });*/
                                break;
                            case MotionEvent.ACTION_UP:
                                //启动广告
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                                break;
                            case MotionEvent.ACTION_CANCEL: //取消事件
                                //启动广告
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                                break;
                        }
                        return false;
                    }
                });
            }
        } else {
            //加载更多数据
            ArrayList<NewsTabBean.NewsData> moreNews = newsTabBean.data.news;
            mNewsList.addAll(moreNews); //将数据追加到原来的集合中
            //刷新ListView
            mNewsAdapter.notifyDataSetChanged();
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

            //要将被点击的view对象文字改为黑色
            String readIds = PrefUtils.getString(mActivity, "read_ids", "");
            if (readIds.contains(news.id + "")) {
                holder.tvTitle.setTextColor(Color.GRAY);

            } else {
                holder.tvTitle.setTextColor(Color.BLACK);

            }

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
