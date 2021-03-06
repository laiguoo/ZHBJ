package com.example.as.zhbj.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 本地缓存
 */
public class LocalCacheUtils {

    private static final String LOCAL_CACHE_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/zhbj74_cache";


    public void setLocalCache(String url, Bitmap bitmap) {
        File dir = new File(LOCAL_CACHE_PATH);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();    //创建文件夹
        }
        try {
            String fileName = MD5Encoder.encode(url);
            File cacheFile = new File(dir, fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    new FileOutputStream(cacheFile));   //参1：图片格式，参2：压缩比例0-100；参3：输出流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getLocalCache(String url) {
        try {
            File cacheFile = new File(LOCAL_CACHE_PATH, MD5Encoder.encode(url));

            if (cacheFile.exists()) {
//                Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(cacheFile));
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(cacheFile));
                return bitmap;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
