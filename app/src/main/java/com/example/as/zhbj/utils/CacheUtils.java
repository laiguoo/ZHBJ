package com.example.as.zhbj.utils;

import android.content.Context;

/**
 * 网络缓存工具类
 */
public class CacheUtils {

    /**
     * 保存到本地
     *
     * @param url
     * @param json
     */
    public static void setCache(Context ctx, String url, String json) {
        PrefUtils.setString(ctx, url, json);
    }

    /**
     * 获取缓存
     *
     * @param ctx
     * @param url
     * @return
     */
    public static String getCache(Context ctx, String url) {
        return PrefUtils.getString(ctx, url, null);
    }
}
