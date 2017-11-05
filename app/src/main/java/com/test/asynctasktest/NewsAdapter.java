package com.test.asynctasktest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lady_zhou on 2017/10/29.
 */

public class NewsAdapter extends BaseAdapter {
    private List<NewsBean> mList;
    private LayoutInflater mInflater;

    public NewsAdapter(Context context, List<NewsBean> data) {
        mList = data;
        mInflater = LayoutInflater.from(context);
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
        new ImageLoader().showImageByThread(viewHolder.mIvIcon, url);
        viewHolder.mTvTitle.setText(mList.get(i).getNewsTitle());
        viewHolder.mTvContent.setText(mList.get(i).getNewsContent());
        return view;
    }

    class ViewHolder {
        public TextView mTvTitle;
        public TextView mTvContent;
        public ImageView mIvIcon;

    }
}
