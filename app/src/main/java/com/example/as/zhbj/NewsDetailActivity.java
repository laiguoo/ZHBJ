package com.example.as.zhbj;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.as.zhbj.global.GlobalConstants;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

/**
 * 新闻详情页
 */
@ContentView(R.layout.activity_news_detail)
public class NewsDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NewsDetailActivity";

    @ViewInject(R.id.ll_control)
    private LinearLayout llControl;
    @ViewInject(R.id.btn_back)
    private ImageButton btnBack;
    @ViewInject(R.id.btn_share)
    private ImageButton btnShare;
    @ViewInject(R.id.btn_text_size)
    private ImageButton btnTextSize;
    @ViewInject(R.id.btn_menu)
    private ImageButton btnMenu;
    @ViewInject(R.id.wv_news_detail)
    private WebView mWebView;
    @ViewInject(R.id.pb_loading)
    private ProgressBar pbLoading;
    private String mUrl;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_news_detail);

        x.view().inject(this);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        /*llControl = (LinearLayout) findViewById(R.id.ll_control);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnShare = (ImageButton) findViewById(R.id.btn_share);
        btnTextSize = (ImageButton) findViewById(R.id.btn_text_size);
        btnMenu = (ImageButton) findViewById(R.id.btn_menu);*/
        llControl.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        btnMenu.setVisibility(View.GONE);

        btnBack.setOnClickListener(this);
        btnTextSize.setOnClickListener(this);
        btnShare.setOnClickListener(this);

        mUrl = getIntent().getStringExtra("url");

//        mWebView.loadUrl("http://www.itcast.cn/");
        mWebView.loadUrl(GlobalConstants.SERVER_URL + mUrl);

        WebSettings settings = mWebView.getSettings();
        settings.setBuiltInZoomControls(true);  //显示缩放按钮(wap网页不支持)
        settings.setUseWideViewPort(true);      //支持双击缩放(wap网页不支持)
        settings.setJavaScriptEnabled(true);    //支持js功能
        mWebView.setWebViewClient(new WebViewClient() {
            //开始加载网页
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i(TAG, "开始加载网页了");
                pbLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i(TAG, "网页加载结束了");
                pbLoading.setVisibility(View.INVISIBLE);

            }

            //所有连接跳转会走此方法
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(String.valueOf(request.getUrl()));
                }*/
                Log.i(TAG, "跳转链接" + request.getUrl());
                return super.shouldOverrideUrlLoading(view, request);

            }
        });

        mWebView.goBack();  //跳到上一个页面
        mWebView.goForward();//跳到下一个页面

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //进度发生变化
                Log.i(TAG, "进度 " + newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //网页标题
                Log.i(TAG, "网页标题" + title);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_text_size:
                //修改网页字体大小
                showChooseDialog();
                break;
            case R.id.btn_share:
                showShare();
                break;
        }
    }

    private int mTempWhich; //记录临时选择的字体大小（点击确定之前的）
    private int mCurrentWhich = 2;  //记录当前选中的字体大小（确定之后的）

    /**
     * 展示选择字体大小的弹窗
     */
    private void showChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("字体大小");
        String[] items = new String[]{"超大号字体", "大号字体", "正常字体", "小号字体", "超小号字体"};
        builder.setSingleChoiceItems(items, mCurrentWhich, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTempWhich = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //根据选择字体大小来修改网页字体大小

                WebSettings settings = mWebView.getSettings();

                switch (mTempWhich) {
                    case 0:
                        //超大号字体
                        settings.setTextZoom(200);
                        break;
                    case 1:
                        //大号字体
                        settings.setTextZoom(150);
                        break;
                    case 2:
                        //正常字体
                        settings.setTextZoom(100);
                        break;
                    case 3:
                        //小号字体
                        settings.setTextZoom(50);
                        break;
                    case 4:
                        //超小号字体
                        settings.setTextZoom(25);
                        break;
                }
                mCurrentWhich = mTempWhich;
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * onekeyshare分享调用九宫格方法
     */
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();

        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("分享标题");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
