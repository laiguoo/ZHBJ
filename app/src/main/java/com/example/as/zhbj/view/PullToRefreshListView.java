package com.example.as.zhbj.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.as.zhbj.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 下拉刷新的ListView
 */
public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener{

    private static final String TAG = "PullToRefreshListView";

    private static final int STATE_PULL_TO_REFRESH = 1;
    private static final int STATE_RELEASE_TO_REFRESH = 2;
    private static final int STATE_REFRESHING = 3;

    private int mCurrentState = STATE_PULL_TO_REFRESH;

    private View mHeaderView;
    private int startY = -1;
    private int mMeasuredHeight;
    private TextView tvTitle;
    private TextView tvTime;
    private ImageView ivArrow;
    private RotateAnimation animUp;
    private RotateAnimation animDown;
    private ProgressBar pbProgress;
    private OnRefreshListener mListener;
    private View mFooterView;
    private int mFooterViewMeasuredHeight;

    public PullToRefreshListView(Context context) {
        super(context);
        initHeaderView();
        initFooterView();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();


    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView();
        initFooterView();


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initHeaderView();
        initFooterView();


    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.pull_to_refresh_header, null);
        this.addHeaderView(mHeaderView);

        tvTitle = mHeaderView.findViewById(R.id.tv_title);
        tvTime = mHeaderView.findViewById(R.id.tv_time);
        ivArrow = mHeaderView.findViewById(R.id.iv_arrow);

        pbProgress = mHeaderView.findViewById(R.id.pb_loading);

        //隐藏1头布局
        mHeaderView.measure(0, 0);
        mMeasuredHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0, -mMeasuredHeight, 0, 0);

        initAni();

        setCurrentTime();

    }

    //初始化脚布局
    private void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.pull_to_refresh_footer, null);
        this.addFooterView(mFooterView);
        mFooterView.measure(0,0);
        mFooterViewMeasuredHeight = mFooterView.getMeasuredHeight();
        mFooterView.setPadding(0, -mFooterViewMeasuredHeight, 0, 0);

        this.setOnScrollListener(this); //设置滑动监听

    }

    //设置刷新时间
    private void setCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(new Date());

        tvTime.setText(time);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                startY = (int) ev.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                if (startY == -1) {     //当用户按住头条新闻的ViewPager进行下拉时，
                    // ACTION_DOWN会被ViewPager消费掉,导致startY没有赋值,此处需要重新获取一下
                    startY = (int) ev.getY();
                }

                if (mCurrentState == STATE_REFRESHING) {
                    break;
                }

                int endY = (int) ev.getY();

                int dy = endY - startY;

                int firstVisiblePosition = getFirstVisiblePosition();   //当前显示的第一个item的位置

                //必须下拉，并且当前显示的是第一个
                if (dy > 0 && firstVisiblePosition == 0) {
                    int padding = dy - mMeasuredHeight; //计算当前下拉控件的padding
                    mHeaderView.setPadding(0, padding, 0, 0);

                    if (padding > 0 && mCurrentState != STATE_RELEASE_TO_REFRESH) {
                        //改为松开刷新
                        mCurrentState = STATE_RELEASE_TO_REFRESH;
                    } else if (padding < 0 && mCurrentState != STATE_PULL_TO_REFRESH) {
                        //改为下拉刷新
                        mCurrentState = STATE_PULL_TO_REFRESH;
                    } else {
                        return true;
                    }
                    refreshState();

                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                startY = -1;
                if (mCurrentState == STATE_RELEASE_TO_REFRESH) {
                    mCurrentState = STATE_REFRESHING;
                    refreshState();
                    //完整展示头布局
                    mHeaderView.setPadding(0,0,0,0);

                    //进行回调
                    if (mListener != null) {
                        mListener.onRefresh();
                    }

                } else if (mCurrentState == STATE_PULL_TO_REFRESH) {
                    //隐藏头布局
                    mHeaderView.setPadding(0, -mMeasuredHeight, 0, 0);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 初始化箭头动画
     */
    private void initAni() {
        animUp = new RotateAnimation(0, -180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(200);
        animUp.setFillAfter(true);

        animDown = new RotateAnimation(-180, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animDown.setDuration(200);
        animDown.setFillAfter(true);

    }

    /**
     * 根据当前状态刷新界面
     */
    private void refreshState() {
        switch (mCurrentState) {
            case STATE_PULL_TO_REFRESH:
                tvTitle.setText("下拉刷新");
                ivArrow.startAnimation(animDown);
                pbProgress.setVisibility(View.INVISIBLE);
                ivArrow.setVisibility(View.VISIBLE);
                break;
            case STATE_RELEASE_TO_REFRESH:
                tvTitle.setText("松开刷新");
                ivArrow.startAnimation(animUp);
                pbProgress.setVisibility(View.INVISIBLE);
                ivArrow.setVisibility(View.VISIBLE);
                break;
            case STATE_REFRESHING:
                tvTitle.setText("正在刷新...");
                ivArrow.clearAnimation();   //清楚动画后才能隐藏
                pbProgress.setVisibility(VISIBLE);
                ivArrow.setVisibility(INVISIBLE);

                break;

        }
    }

    /**
     * 刷新结束，收起控件
     */
    public void onRefreshCompleted(boolean success) {
        if (!isLoadMore) {
            mHeaderView.setPadding(0, -mMeasuredHeight, 0, 0);
            tvTitle.setText("下拉刷新");
            mCurrentState = STATE_PULL_TO_REFRESH;
            pbProgress.setVisibility(View.INVISIBLE);
            ivArrow.setVisibility(View.VISIBLE);

            if (success) {  //只有刷新成功才更新时间
                setCurrentTime();
            }
        } else {
            //加载更多
            mFooterView.setPadding(0, -mFooterViewMeasuredHeight, 0, 0);//隐藏布局
            isLoadMore = false;

        }


    }

    /**
     * 暴露接口,设置监听
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    private boolean isLoadMore; //标记是否正在加载更多

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            int lastVisiblePosition = getLastVisiblePosition();

            Log.i(TAG, "onScrollStateChanged: " + getCount());
            Log.i(TAG, "onScrollStateChanged: " + getHeaderViewsCount());
            Log.i(TAG, "onScrollStateChanged: " + getFooterViewsCount());

            int count = getCount();
            Log.i(TAG, "onScrollStateChanged:11111 " + count);
            if (lastVisiblePosition == count - 1 && !isLoadMore) {   //
                Log.i(TAG, "onScrollStateChanged: 加载更多.....");
                isLoadMore = true;
                mFooterView.setPadding(0, 0, 0, 0);
                setSelection(getCount() - 1);
                Log.i(TAG, "onScrollStateChanged: " + getCount());
                //通知主界面加载下一页
                if (mListener != null) {
                    mListener.onLoadMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /**
     * 下拉刷新回调接口
     */
    public interface OnRefreshListener{
        public void onRefresh();

        //下拉加载更多
        public void onLoadMore();
    }
}
