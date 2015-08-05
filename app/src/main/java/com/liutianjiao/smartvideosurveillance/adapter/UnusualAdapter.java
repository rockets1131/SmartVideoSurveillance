package com.liutianjiao.smartvideosurveillance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

    ;

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
        NetworkImageView image1, image2, image3;
        final int curPosition = position;
        if (convertView == null)
            convertView = (RelativeLayout)LayoutInflater.from(context)
                    .inflate(R.layout.unusual_style, null);
        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        userName.setText(unusualList.get(position).userName);
        TextView unusualTime = (TextView) convertView
                .findViewById(R.id.unusual_time);
        unusualTime.setText(unusualList.get(position).unusualTime);
        if (unusualList.get(position).content != null
                && !unusualList.get(position).content.isEmpty()) {
            TextView content = (TextView) convertView.findViewById(R.id.content);
            content.setText(unusualList.get(position).content);
            content.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < 3; i++) {

            switch (i) {
                case 0: {
                    image1 = (NetworkImageView) convertView.findViewById(R.id.temp_pic1);
                    if (unusualList.get(position).picPath != null) {
                        image1.setVisibility(View.VISIBLE);
                        String imageUrl = Config.WEB_ADDRESS + unusualList.get(position).picPath + "small/" + unusualList.get(position).picFiles[i];
                        image1.setDefaultImageResId(R.drawable.add_normal);
                        image1.setErrorImageResId(R.drawable.load_error);
                        image1.setImageUrl(imageUrl, imageLoader);
                    }
                    else
                        image1.setVisibility(View.GONE);
                    break;
                }
                case 1: {
                    image2 = (NetworkImageView) convertView.findViewById(R.id.temp_pic2);
                    if (unusualList.get(position).picPath != null && unusualList.get(position).picFiles.length > 1) {
                        image2.setVisibility(View.VISIBLE);
                            String imageUrl = Config.WEB_ADDRESS + unusualList.get(position).picPath + "small/" + unusualList.get(position).picFiles[i];
                        image2.setDefaultImageResId(R.drawable.add_normal);
                        image2.setErrorImageResId(R.drawable.load_error);
                        image2.setImageUrl(imageUrl, imageLoader);
                    }
                    else
                        image2.setVisibility(View.GONE);
                    break;
                }
                case 2: {
                    image3 = (NetworkImageView) convertView.findViewById(R.id.temp_pic3);
                    if (unusualList.get(position).picPath != null && unusualList.get(position).picFiles.length > 2) {
                        image3.setVisibility(View.VISIBLE);
                        String imageUrl = Config.WEB_ADDRESS + unusualList.get(position).picPath + "small/" + unusualList.get(position).picFiles[i];
                        image3.setDefaultImageResId(R.drawable.add_normal);
                        image3.setErrorImageResId(R.drawable.load_error);
                        image3.setImageUrl(imageUrl, imageLoader);
                    }
                    else
                        image3.setVisibility(View.GONE);
                }
            }
        }
        return convertView;
    }
}
