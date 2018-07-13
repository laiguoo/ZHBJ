package com.example.as.zhbj.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.as.zhbj.MainActivity;
import com.example.as.zhbj.R;
import com.example.as.zhbj.base.impl.NewsCenterPager;
import com.example.as.zhbj.domain.NewsMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xutils.ViewInjector;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 侧边栏
 */
public class LeftMenuFragment extends BaseFragment {

    private ListView lvList;
    private int mCurrentPos;

    private ArrayList<NewsMenu.NewsMenuData> mNewsMenuData; //侧边栏网络数据对象
    private LeftMenuAdapter mAdapter;


    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_left_menu, null);
        lvList = view.findViewById(R.id.lv_list);
//        lvList.setDividerHeight(0);
//        x.view().inject(mActivity, view);
        return view;
    }

    @Override
    public void initData() {

    }

    /**
     * 给侧边栏设置对象
     *
     * @param data
     */
    public void setMenuData(ArrayList<NewsMenu.NewsMenuData> data) {
        mCurrentPos = 0;//当前选中的页面要归零
        //更新页面
        mNewsMenuData = data;
        mAdapter = new LeftMenuAdapter();
        lvList.setAdapter(mAdapter);
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPos = position;
                mAdapter.notifyDataSetChanged();//刷新ListView
                //收起侧边栏
                toggle();
                //侧边栏点击之后要修改新闻中心的FrameLayout
                setCurrentDetailPager(position);
            }
        });

    }

    /**
     * 设置当前的菜单详情页
     *
     * @param position
     */
    private void setCurrentDetailPager(int position) {
        //获取新闻中心的对象
        MainActivity mainUI = (MainActivity) mActivity;

        //获取ContentFragment
        ContentFragment contentFragment = mainUI.getContentFragment();
        //获取NewsCenterPager
        NewsCenterPager newsCenterPager = contentFragment.getNewsCenterPager();
        //修改新闻中心的FrameLayout布局
        newsCenterPager.setCurrentDetailPager(position);

    }

    /**
     * 打开或者关闭侧边栏
     */
    private void toggle() {
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        slidingMenu.toggle();//将侧边栏的开启状态取反
    }

    class LeftMenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNewsMenuData.size();
        }

        @Override
        public NewsMenu.NewsMenuData getItem(int position) {
            return mNewsMenuData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(mActivity, R.layout.list_item_left_menu, null);
            TextView tvMenu = view.findViewById(R.id.tv_menu);
            NewsMenu.NewsMenuData item = getItem(position);
            tvMenu.setText(item.title);
            if (mCurrentPos == position) {
                //被选中  文字变为红色
                tvMenu.setEnabled(true);
            } else {
                //未选中，文字变为白色
                tvMenu.setEnabled(false);
            }
            return view;
        }
    }
}
