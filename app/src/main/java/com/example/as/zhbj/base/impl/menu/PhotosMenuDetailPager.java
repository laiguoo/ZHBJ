package com.example.as.zhbj.base.impl.menu;

import android.app.Activity;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.as.zhbj.R;
import com.example.as.zhbj.base.BaseMenuDetailPager;
import com.example.as.zhbj.domain.PhotosBean;
import com.example.as.zhbj.global.GlobalConstants;
import com.example.as.zhbj.utils.CacheUtils;
import com.example.as.zhbj.utils.MyBitmapUtils;
import com.example.as.zhbj.utils.NetCacheUtils;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 菜单详情页--组图
 */
public class PhotosMenuDetailPager extends BaseMenuDetailPager implements View.OnClickListener {
    private static final String TAG = "PhotosMenuDetailPager";

    private ImageButton btnPhoto;
    private ListView lvPhoto;
    private GridView gvPhoto;
    private ArrayList<PhotosBean.PhotoNews> mNewsList;

    public PhotosMenuDetailPager(Activity activity, ImageButton btnPhoto) {
        super(activity);
        this.btnPhoto = btnPhoto;
        btnPhoto.setOnClickListener(this);  //组图按钮切换事件
        Log.i(TAG, "PhotosMenuDetailPager: " +
                Formatter.formatFileSize(mActivity, Runtime.getRuntime().maxMemory()));
    }

    @Override
    public View initView() {
        //给帧布局填充对象
        View view = View.inflate(mActivity, R.layout.pager_phone_menu_detail, null);
        lvPhoto = view.findViewById(R.id.lv_photo);
        gvPhoto = view.findViewById(R.id.gv_photo);
        return view;
    }

    @Override
    public void initData() {
        String cache = CacheUtils.getCache(mActivity, GlobalConstants.PHOTO_URL);
        if (!TextUtils.isEmpty(cache)) {
            processData(cache);
        }
        getDataFromServer();
    }

    private void getDataFromServer() {
        RequestParams params = new RequestParams(GlobalConstants.PHOTO_URL);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processData(result);
                CacheUtils.setCache(mActivity, GlobalConstants.PHOTO_URL, result);
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
        PhotosBean photosBean = gson.fromJson(result, PhotosBean.class);
        mNewsList = photosBean.data.news;

        lvPhoto.setAdapter(new PhotoAdapter());
        gvPhoto.setAdapter(new PhotoAdapter());//GridView与ListView布局完全一致，
        // 所以可以共用一个Adapter

    }

    private boolean isListView;//标记当前是否是ListView

    @Override
    public void onClick(View v) {
        if (isListView) {
            lvPhoto.setVisibility(View.GONE);
            gvPhoto.setVisibility(View.VISIBLE);
            isListView = false;
            btnPhoto.setImageResource(R.drawable.icon_pic_list_type);
        } else {
            lvPhoto.setVisibility(View.VISIBLE);
            gvPhoto.setVisibility(View.GONE);
            btnPhoto.setImageResource(R.drawable.icon_pic_grid_type);
            isListView = true;
        }
    }

    class PhotoAdapter extends BaseAdapter {
        private final MyBitmapUtils myBitmapUtils;
        //        private ImageOptions mImageOptions;

        public PhotoAdapter() {
           /* ImageOptions.Builder builder = new ImageOptions.Builder();
            builder.setLoadingDrawableId(R.drawable.pic_item_list_default);
            mImageOptions = builder.build();*/
            myBitmapUtils = new MyBitmapUtils();
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public PhotosBean.PhotoNews getItem(int position) {
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
                convertView = View.inflate(mActivity, R.layout.list_item_photo, null);
                holder = new ViewHolder();
                holder.ivPic = convertView.findViewById(R.id.iv_pic);
                holder.tvTitle = convertView.findViewById(R.id.tv_title);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            PhotosBean.PhotoNews item = getItem(position);
            holder.tvTitle.setText(item.title);
            /*x.image().bind(holder.ivPic, GlobalConstants.SERVER_URL + item.listimage,
                    mImageOptions);*/

            myBitmapUtils.display(holder.ivPic, GlobalConstants.SERVER_URL + item.listimage);
            return convertView;
        }
    }

    static class ViewHolder {
        public ImageView ivPic;
        public TextView tvTitle;
    }


}
