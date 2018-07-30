package com.example.as.zhbj;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.as.zhbj.fragment.ContentFragment;
import com.example.as.zhbj.fragment.LeftMenuFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.umeng.analytics.MobclickAgent;

import org.xutils.x;

public class MainActivity extends SlidingFragmentActivity {

    public static final String TAG_LEFT_MENU = "tag_left_menu";
    public static final String TAG_CONTENT = "tag_content";
    private FragmentManager fm;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        setBehindContentView(R.layout.left_menu);
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);    //全屏触摸
//        slidingMenu.setBehindOffset(200);   //屏幕预留200像素宽度

        WindowManager windowManager = getWindowManager();
        int width = windowManager.getDefaultDisplay().getWidth();
        slidingMenu.setBehindOffset(width * 200 / 320);

        initFragment();
//        x.view().inject(this);
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();//开启事务
        //用Fragment替换帧布局
        transaction.replace(R.id.fl_left_menu, new LeftMenuFragment(), TAG_LEFT_MENU);
        transaction.replace(R.id.fl_main, new ContentFragment(), TAG_CONTENT);
        transaction.commit();
    }

    /**
     * 获取侧边栏Fragment对象
     * @return
     */
    public LeftMenuFragment getLeftMenuFragment() {
        if (fm == null) {
            fm = getSupportFragmentManager();
        }
        return (LeftMenuFragment) fm.findFragmentByTag(TAG_LEFT_MENU);
    }

    /**
     * 获取主页Fragment对象
     *
     * @return
     */
    public ContentFragment getContentFragment() {
        if (fm == null) {
            fm = getSupportFragmentManager();
        }
        return (ContentFragment) fm.findFragmentByTag(TAG_CONTENT);
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
