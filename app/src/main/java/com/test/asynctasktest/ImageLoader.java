package com.test.asynctasktest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lady_zhou on 2017/11/5.
 */

public class ImageLoader {
    private ImageView mImageView;
    private String mUrl;
    //创建cache
    private LruCache<String, Bitmap> mCaches;
    //同过tag在listView中寻找imageView
    private ListView mListView;
    //创建一个集合去管理AsyncTak的task
    private Set<NewsAsyncTask> mTask;

    public ImageLoader(ListView listView) {
        //初始化
        mListView = listView;
        mTask = new HashSet<>();

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;//缓存大小
        mCaches = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
//                return super.sizeOf(key, value);
                return value.getByteCount();//在每次使用缓存的时候调用
            }
        };
    }

    /**
     * 增加到缓存
     *
     * @param url
     * @param bitmap
     */
    public void addBitmapToCache(String url, Bitmap bitmap) {
        //校验当前缓存是否存在
        if (getBitmapFromCache(url) == null) {
            mCaches.put(url, bitmap);
        }
    }

    /**
     * 从缓存中获取数据
     *
     * @param url
     * @return
     */
    public Bitmap getBitmapFromCache(String url) {
        return mCaches.get(url);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

    public void showImageByThread(ImageView imageView, final String url) {
        mImageView = imageView;
        mUrl = url;
        new Thread() {
            @Override
            public void run() {
                super.run();
                Bitmap bitmap = getBitmapFromURl(url);

                //将bitmap以Message的形式发送出去
                Message message = Message.obtain();// Message.obtain()这种方法可以使用已有的已回收的Message，提高Message的使用效率
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    /**
     * 通过传递进来的urlString转化成bitmap
     *
     * @param urlString
     * @return
     */
    public Bitmap getBitmapFromURl(String urlString) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            Thread.sleep(1000);//模拟网络不好的情况
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void showImageByAsyncTak(ImageView imageView, String url) {
        //从缓存中取出对应的图片
        Bitmap bitmap = getBitmapFromCache(url);
        //如果缓存中没有，那么必须去下载
        if (bitmap == null) {
            new NewsAsyncTask(url).execute(url);
        } else {
            //因为还在主线程中所以直接使用imageView
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * 取消task中的任务
     */
    public void cancelAllTssks() {
        if (mTask != null) {
            for (NewsAsyncTask task : mTask) {
                task.cancel(false);
            }
        }
    }

    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {
        //        private ImageView mImageView;
        private String mUrl;

        public NewsAsyncTask(String url) {
//        public NewsAsyncTask(ImageView imageView, String url) {
//            mImageView = imageView;
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            //从网络获取图片
            Bitmap bitmap = getBitmapFromURl(url);
            if (bitmap != null) {
                //将不在缓存中的图片加入缓存
                addBitmapToCache(url, bitmap);
            }
            return bitmap;
//            return getBitmapFromURl(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
//            if (mImageView.getTag().equals(mUrl)) {
//                mImageView.setImageBitmap(bitmap);
//            }
            ImageView imageView = mListView.findViewWithTag(mUrl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            mTask.remove(this);
        }

    }

    /**
     * 用来加载从start到end的所有图片
     *
     * @param start
     * @param end
     */
    public void loadImages(int start, int end) {
        for (int i = start; i < end; i++) {
            String url = NewsAdapter.URLS[i];//获得从start开始的图片的url
            Bitmap bitmap = getBitmapFromCache(url);
            //如果缓存中没有，那么必须去下载
            if (bitmap == null) {
                NewsAsyncTask task = new NewsAsyncTask(url);
                task.execute(url);
                mTask.add(task);
//                new NewsAsyncTask(imageView, url).execute(url);
            } else {
                ImageView imageView = mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
