package com.example.as.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.as.zhbj.MainActivity;
import com.example.as.zhbj.base.BaseMenuDetailPager;
import com.example.as.zhbj.base.BasePager;
import com.example.as.zhbj.base.impl.menu.InteractMenuDetailPager;
import com.example.as.zhbj.base.impl.menu.NewsMenuDetailPager;
import com.example.as.zhbj.base.impl.menu.PhotosMenuDetailPager;
import com.example.as.zhbj.base.impl.menu.TopicMenuDetailPager;
import com.example.as.zhbj.domain.NewsMenu;
import com.example.as.zhbj.fragment.LeftMenuFragment;
import com.example.as.zhbj.global.GlobalConstants;
import com.example.as.zhbj.utils.CacheUtils;
import com.example.as.zhbj.utils.PrefUtils;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 新闻中心
 */
public class NewsCenterPager extends BasePager {
    private static final String TAG = "HomePager";

    private ArrayList<BaseMenuDetailPager> mMenuDetailPagers;
    private NewsMenu mNewsData; //分类信息网络数据

    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        Log.i(TAG, "新闻中心初始化了.....");
        /*//给帧布局填充对象
        TextView textView = new TextView(mActivity);
        textView.setText("新闻中心");
        textView.setTextColor(Color.RED);
        textView.setTextSize(22);
        textView.setGravity(Gravity.CENTER);

        flContent.addView(textView);*/

        //修改标题
        tvTitle.setText("新闻");

        //先判断有没有缓存，如果有的话，先获取缓存
        String cache = CacheUtils.getCache(mActivity, GlobalConstants.CATEGORY_URL);
        if (!TextUtils.isEmpty(cache)) {
            Log.i(TAG, "发现缓存了...");
            processData(cache);
        }

        //请求服务器，获取数据
        //开源框架
        getDataFromServer();

    }

    /**
     * 从服务器获取数据
     */
    private void getDataFromServer() {
//        JSONObject js_request = new JSONObject();//服务器需要传参的json对象
        RequestParams params = new RequestParams(GlobalConstants.CATEGORY_URL);
       /* params.setAsJsonContent(true);
        params.setBodyContent(js_request.toString());*/
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "服务器返回结果：" + result);
                processData(result);
                //写缓存
                CacheUtils.setCache(mActivity, GlobalConstants.CATEGORY_URL, result);
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

    /**
     * 解析数据
     */
    private void processData(String json) {
        Gson gson = new Gson();
        mNewsData = gson.fromJson(json, NewsMenu.class);
        Log.i(TAG, "解析结果：" + mNewsData);

        //获取侧边栏对象
        MainActivity mainUI = (MainActivity) mActivity;
        LeftMenuFragment leftMenuFragment = mainUI.getLeftMenuFragment();

        //给侧边栏设置对象
        leftMenuFragment.setMenuData(mNewsData.data);
        //初始化四个菜单详情页
        mMenuDetailPagers = new ArrayList<>();
        mMenuDetailPagers.add(new NewsMenuDetailPager(mActivity,mNewsData.data.get(0).children));
        mMenuDetailPagers.add(new TopicMenuDetailPager(mActivity));
        mMenuDetailPagers.add(new PhotosMenuDetailPager(mActivity,btnPhoto));
        mMenuDetailPagers.add(new InteractMenuDetailPager(mActivity));

        //将新闻菜单详情页设置为默认页面
        setCurrentDetailPager(0);

    }

    /**
     * 设置菜单详情页
     */
    public void setCurrentDetailPager(int position) {
        //重新给FrameLayout添加内容
        BaseMenuDetailPager pager = mMenuDetailPagers.get(position);  //获取当前应该显示的页面
        View mRootView = pager.mRootView;   //当前页面得布局
        flContent.removeAllViews(); //添加给帧布局
        flContent.addView(mRootView);

        //初始化页面数据
        pager.initData();

        //更新标题
        NewsMenu.NewsMenuData newsMenuData = mNewsData.data.get(position);
        tvTitle.setText(newsMenuData.title);

        //如果是组图页面需要显示切换按钮
        if (pager instanceof PhotosMenuDetailPager) {
            btnPhoto.setVisibility(View.VISIBLE);
        } else {
            //隐藏切换按钮
            btnPhoto.setVisibility(View.GONE);
        }

    }
}

