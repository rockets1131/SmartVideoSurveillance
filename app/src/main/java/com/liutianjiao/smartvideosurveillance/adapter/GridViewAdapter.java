package com.liutianjiao.smartvideosurveillance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.liutianjiao.smartvideosurveillance.R;
import com.liutianjiao.smartvideosurveillance.base.Config;

/**
 * Created by rockets1131 on 2015/8/22.
 */
public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private String pictureUrl[];
    private String picturePath;
    private ImageLoader imageLoader;

    public GridViewAdapter(Context context, String pictureUrl[], String picturePath, ImageLoader imageLoader) {
        this.context = context;
        this.pictureUrl = pictureUrl;
        this.picturePath = picturePath;
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return pictureUrl.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.girdview_item, parent, false);
        NetworkImageView networkImageView = (NetworkImageView) convertView.findViewById(R.id.temp_pic);
        String imageUrl = Config.WEB_ADDRESS + picturePath + "small/" + pictureUrl[position];
        networkImageView.setErrorImageResId(R.drawable.load_error);
        networkImageView.setImageUrl(imageUrl, imageLoader);
        return convertView;
    }
}
