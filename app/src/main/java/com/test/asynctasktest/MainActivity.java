package com.test.asynctasktest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private static String url = "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.lv_main);
        new NewAsyncTask().execute(url);
    }

    class NewAsyncTask extends AsyncTask<String, Void, List<NewsBean>> {

        @Override
        protected List<NewsBean> doInBackground(String... strings) {
            return getJsonData(strings[0]);
        }
    }

    private List<NewsBean> getJsonData(String string) {
        List<NewsBean> newsBeanList = new ArrayList<>();
        try {
            String jsonString = readStream(new URL(url).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newsBeanList;
    }

    private String readStream(InputStream is) {
        InputStreamReader isr;
        String result = "";
        try {
            String line = "";
            isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);//将字节流转换为字符流
            try {
                while ((line = br.readLine()) != null) {
                    result += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
