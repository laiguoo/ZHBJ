package com.example.as.zhbj.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * 网络缓存
 */
public class NetCacheUtils {
    private static final String TAG = "NetCacheUtils";
    private final LocalCacheUtils mLocalCacheUtils;
    private final MemoryCacheUtils mMemoryCacheUtils;

    public NetCacheUtils(LocalCacheUtils mLocalCacheUtils, MemoryCacheUtils mMemoryCacheUtils) {
        this.mLocalCacheUtils = mLocalCacheUtils;
        this.mMemoryCacheUtils = mMemoryCacheUtils;
    }

    public void getBitmapFromNet(ImageView imageView, String url) {
//        AsyncTask 异步封装的工具，可以实现异步请求及主界面更新（线程池+handler的封装）
        new BitmapTask().execute(imageView, url);//启动AsyncTask
    }

    /**
     * 三个泛型意义：
     * 第一个泛型：doInBackground里的参数类型
     * 第二个泛型：onProgressUpdate里的参数类型
     * 第三个泛型：onPostExecute里的参数类型及doInBackground里的返回类型
     *
     */
    class BitmapTask extends AsyncTask<Object, Integer, Bitmap> {

        private ImageView imageView;
        private String url;

        //预加载，运行在主线程
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "onPreExecute: ");
            
        }

        //正在加载，运行在子线程(核心方法），可以直接异步请求
        @Override
        protected Bitmap doInBackground(Object... voids) {
            Log.i(TAG, "doInBackground: ");
            imageView = (ImageView) voids[0];
            url = (String) voids[1];

            imageView.setTag(url);//将当前ImageView与url绑定

            //开始下载图片
            Bitmap bitmap = download(url);

//            publishProgress(values);  //调用此方法实现进度更新(会回调)


            return bitmap;
        }

        //更新进度的方法，运行在主线程
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.i(TAG, "onProgressUpdate: ");
        }

        //加载结束，运行在主线程（核心方法）,可以直接更新UI
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
                //由于ListView的重用机制，导致imageview可能被多个item共用，
                // 从而可能将错误的图片设置给imageview
                // 所以需要在此处校验，判断是否是正确的图片
                String url = (String) imageView.getTag();

                if (url.equals(this.url)) { //判断图片绑定的url是否是bitmap的url，如果是就正确
                    imageView.setImageBitmap(result);

                    Log.i(TAG, "onPostExecute: 从网络下载图片啦");

                    //写本地缓存
                    mLocalCacheUtils.setLocalCache(url, result);
                    //写内存缓存
                    mMemoryCacheUtils.setMemoryCache(url, result);
                }

            }
        }
    }

    private Bitmap download(String url) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(5000);//读取超时时间
            urlConnection.setConnectTimeout(5000);//链接超时时间
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
                InputStream inputStream = urlConnection.getInputStream();
                //根据输入流生成Bitmap对象
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (urlConnection!= null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

}
