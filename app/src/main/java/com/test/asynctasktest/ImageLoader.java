package com.test.asynctasktest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lady_zhou on 2017/11/5.
 */

public class ImageLoader {
    private ImageView mImageView;
    private String mUrl;

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
//            Thread.sleep(1000);//模拟网络不好的情况
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
// catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void showImageByAsyncTak(ImageView imageView, String url) {
        new NewsAsyncTask(imageView,url).execute(url);
    }

    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView mImageView;
        private String mUrl;

        public NewsAsyncTask(ImageView imageView, String url) {
            mImageView = imageView;
            mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return getBitmapFromURl(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }
}
