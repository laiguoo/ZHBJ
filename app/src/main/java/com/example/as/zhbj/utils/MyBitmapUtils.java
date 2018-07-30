package com.example.as.zhbj.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.example.as.zhbj.R;

/**
 * 自定义三级缓存图片加载工具
 */
public class MyBitmapUtils {
    private static final String TAG = "MyBitmapUtils";

    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;

    public MyBitmapUtils() {
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils();
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
    }

    public void display(ImageView imageView, String url) {

        imageView.setImageResource(R.drawable.pic_item_list_default);

        Bitmap bitmap = mMemoryCacheUtils.getMemoryCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            Log.i(TAG, "display: 从内存加载图片");
//            return;
        }

        bitmap = mLocalCacheUtils.getLocalCache(url);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            Log.i(TAG, "display: 从本地加载图片了");

            //写内存缓存
            mMemoryCacheUtils.setMemoryCache(url, bitmap);

//            return;
        }

        mNetCacheUtils.getBitmapFromNet(imageView, url);
    }
}
