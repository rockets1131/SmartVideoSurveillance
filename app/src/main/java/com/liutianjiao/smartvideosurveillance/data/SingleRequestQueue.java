package com.liutianjiao.smartvideosurveillance.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Lenovo on 2015/4/7.
 */
public class SingleRequestQueue {
    private static RequestQueue requestQueue;
    private SingleRequestQueue(){};

    public static synchronized RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            context = context.getApplicationContext();
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }
}
