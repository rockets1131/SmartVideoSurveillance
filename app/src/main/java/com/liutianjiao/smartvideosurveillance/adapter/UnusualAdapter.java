package com.liutianjiao.smartvideosurveillance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.liutianjiao.smartvideosurveillance.R;
import com.liutianjiao.smartvideosurveillance.base.Config;
import com.liutianjiao.smartvideosurveillance.data.LruImageCache;
import com.liutianjiao.smartvideosurveillance.data.SingleRequestQueue;
import com.liutianjiao.smartvideosurveillance.data.Unusual;

import java.util.List;

public class UnusualAdapter extends BaseAdapter {
    //private LayoutInflater inflater;
    private RequestQueue rQueue;
    private Context context;
    private List<Unusual> unusualList;
    private LruImageCache lruImageCache;
    private ImageLoader imageLoader;

    public UnusualAdapter(Context context, List<Unusual> unusualList) {
        super();
        this.context = context;
        this.unusualList = unusualList;
        lruImageCache = LruImageCache.instance();
        rQueue = SingleRequestQueue.getRequestQueue(context);
        imageLoader = new ImageLoader(rQueue, lruImageCache);
    }

    @Override
    public int getCount() {
        return unusualList.size();
    }

    @Override
    public Unusual getItem(int position) {
        return unusualList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return unusualList.get(position).unusualId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = (RelativeLayout)LayoutInflater.from(context)
                    .inflate(R.layout.unusual_style, parent, false);
        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        userName.setText(unusualList.get(position).userName);
        TextView unusualTime = (TextView) convertView
                .findViewById(R.id.unusual_time);
        unusualTime.setText(unusualList.get(position).unusualTime);
        TextView content = (TextView) convertView.findViewById(R.id.content);
        if (unusualList.get(position).content != null
                && !unusualList.get(position).content.isEmpty()) {
            content.setText(unusualList.get(position).content);
            content.setVisibility(View.VISIBLE);
        } else
            content.setVisibility(View.GONE);
        GridView gridView = (GridView) convertView.findViewById(R.id.gridview);
        if (unusualList.get(position).picPath != null) {
            gridView.setVisibility(View.VISIBLE);
            gridView.setAdapter(new GridViewAdapter(context, unusualList.get(position).picFiles, unusualList.get(position).picPath, imageLoader));
        } else
            gridView.setVisibility(View.VISIBLE);
        return convertView;
    }
}
