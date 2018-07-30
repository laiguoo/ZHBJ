package com.example.as.zhbj.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.text.Format;
import java.util.Formatter;
import java.util.HashMap;

public class MemoryCacheUtils {
    private static final String TAG = "MemoryCacheUtils";

    //    private HashMap<String, Bitmap> mMemoryCache = new HashMap<>();
//    private HashMap<String, SoftReference<Bitmap>> mMemoryCache = new HashMap<>();

    private LruCache<String, Bitmap> mMemoryCache;

    public MemoryCacheUtils() {
        //lru least recentlly used
        long maxMemory = Runtime.getRuntime().maxMemory();
        Log.i(TAG, "maxMemory" + maxMemory);
        mMemoryCache = new LruCache<String, Bitmap>((int) (maxMemory / 8)) {
            /**
             * 返回每个对象的大小
             * @param key
             * @param value
             * @return
             */
            @Override
            protected int sizeOf(String key, Bitmap value) {
                int byteCount = value.getByteCount();
                return byteCount;
            }
        };

    }

    /**
     * 写缓存
     */
    public void setMemoryCache(String url, Bitmap bitmap) {
//        mMemoryCache.put(url, bitmap);
       /* SoftReference<Bitmap> softReference = new SoftReference<Bitmap>(bitmap);//使用软引用将bitmap包装起来
        mMemoryCache.put(url, softReference);*/
        mMemoryCache.put(url, bitmap);

    }

    /**
     * 读缓存
     */
    public Bitmap getMemoryCache(String url) {

//        return mMemoryCache.get(url);
        /*SoftReference<Bitmap> bitmapSoftReference = mMemoryCache.get(url);
        if (bitmapSoftReference != null) {
            Bitmap bitmap = bitmapSoftReference.get();
            return bitmap;
        }*/
//        return null;
        return mMemoryCache.get(url);

    }

}
