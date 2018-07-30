package com.example.as.zhbj;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.as.zhbj.utils.DensityUtils;
import com.example.as.zhbj.utils.PrefUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 新手引导页面
 */
public class GuideActivity extends AppCompatActivity {
    private static final String TAG = "GuideActivity";

    private ViewPager mViewPager;
    private int[] mImageIds = new int[]{R.drawable.guide_1,
            R.drawable.guide_2, R.drawable.guide_3};
    private List<ImageView> mImageViewList;     //ImageView集合
    private LinearLayout llContainer;
    private ImageView ivRedPoint;
    private int mPointDis;
    private Button btnStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        mViewPager = (ViewPager) findViewById(R.id.vp_guide);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        ivRedPoint = (ImageView) findViewById(R.id.iv_red_point);
        btnStart = (Button) findViewById(R.id.btn_start);
        initData();
        mViewPager.setAdapter(new GuideAdapter());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //页面滑动过程中的回调(包括滑行)
                Log.i(TAG, "当前位置： " + position + "偏移百分比：" + positionOffset + "偏移像素："
                        + positionOffsetPixels);
                //更新小红点位置
                int leftMargin = (int) (mPointDis * positionOffset) + position * mPointDis;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                        ivRedPoint.getLayoutParams();
                params.leftMargin = leftMargin; //修改左边距
                ivRedPoint.setLayoutParams(params);

            }

            @Override
            public void onPageSelected(int position) {
                //某个页面被选中
                if (position == mImageViewList.size() - 1) {
                    btnStart.setVisibility(View.VISIBLE);
                } else {
                    btnStart.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //页面状态改变的回调

            }
        });
        //计算两个圆点的距离
        //移动距离 = 第二个left值 - 第一个left值
        //measure --> layout--> draw  (在onCreate之后执行)
       /* mPointDis = llContainer.getChildAt(1).getLeft() -
                llContainer.getChildAt(0).getLeft();
        Log.i(TAG, "mPointDis:" + mPointDis);*/
        //监听layout结束的事件
        //视图树
        ivRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ivRedPoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //layout方法执行结束的回调
                mPointDis = llContainer.getChildAt(1).getLeft() -
                        llContainer.getChildAt(0).getLeft();
                Log.i(TAG, "mPointDis:" + mPointDis);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更新SP
                PrefUtils.setBoolean(getApplicationContext(), "is_first_enter", false);
                //跳转到主页面
                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                finish();
            }
        });

    }

    //初始化数据
    private void initData() {
        mImageViewList = new ArrayList<>();
        for (int i = 0; i < mImageIds.length; i++) {
            ImageView view = new ImageView(this);
            view.setBackgroundResource(mImageIds[i]);   //通过设置背景可以让宽高填充布局
            mImageViewList.add(view);

            //初始化小圆点
            ImageView point = new ImageView(this);
            point.setImageResource(R.drawable.shape_point_gray);    //设置图片（shape形状）
            //初始化布局参数，父控件是谁就用谁的参数
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                //从第二个点开始设置左边距
//                layoutParams.leftMargin = 10;
                layoutParams.leftMargin = DensityUtils.dip2px(10, this);
            }
            point.setLayoutParams(layoutParams);
            llContainer.addView(point);

        }
    }

    class GuideAdapter extends PagerAdapter {

        //item个数
        @Override
        public int getCount() {
            return mImageViewList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        //初始化item布局
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = mImageViewList.get(position);
            container.addView(imageView);
            return imageView;
        }

        //销毁item
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
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
