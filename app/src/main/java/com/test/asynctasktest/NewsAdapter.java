package com.test.asynctasktest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lady_zhou on 2017/10/29.
 */

public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener {
    private List<NewsBean> mList;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private int mStart, mEnd;

    public static String[] URLS;

    public NewsAdapter(Context context, List<NewsBean> data, ListView listView) {
        mList = data;
        mInflater = LayoutInflater.from(context);
        mImageLoader = new ImageLoader(listView);

        URLS = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            URLS[i] = data.get(i).newsIconUrl;//将图片的url转到了静态的数组中
        }
        listView.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_layout, null);
            viewHolder.mIvIcon = view.findViewById(R.id.iv_icon);
            viewHolder.mTvTitle = view.findViewById(R.id.tv_title);
            viewHolder.mTvContent = view.findViewById(R.id.tv_content);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.mIvIcon.setImageResource(R.mipmap.ic_launcher);
        String url = mList.get(i).getNewsIconUrl();
        viewHolder.mIvIcon.setTag(url);//使得图片对应
//        new ImageLoader().showImageByThread(viewHolder.mIvIcon, url);
        //这样写每次都创建了一个新的LruCache
//        new ImageLoader().showImageByAsyncTak(viewHolder.mIvIcon, url);
        mImageLoader.showImageByAsyncTak(viewHolder.mIvIcon, url);
        viewHolder.mTvTitle.setText(mList.get(i).getNewsTitle());
        viewHolder.mTvContent.setText(mList.get(i).getNewsContent());
        return view;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        //滚动状态是停止
        if (i == SCROLL_STATE_IDLE) {
            //加载可见项
            mImageLoader.loadImages(mStart,mEnd);
        } else {
            //停止加载
            mImageLoader.cancelAllTssks();
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //firstVisibleItem第一个可见元素，visibleItemCount可见元素的长度
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
    }

    class ViewHolder {
        public TextView mTvTitle;
        public TextView mTvContent;
        public ImageView mIvIcon;

    }
}
