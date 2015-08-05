package com.liutianjiao.smartvideosurveillance;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.liutianjiao.smartvideosurveillance.base.Config;

public class MainActivity extends ActionBarActivity {
    private TextView map;
    private TextView unusual;
    private Fragment unusualFragment;
    private Fragment mapFragment;
    private FragmentTransaction transaction;
    private Drawable mapDrawable;
    private Drawable unusualDrawable;
    private Button add;

    private DisplayMetrics displayMetrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Config.DISPLAY_WIDTH = displayMetrics.widthPixels;
        Config.DISPLAY_HEIGHT = displayMetrics.heightPixels;
        //Intent heartbeat = new Intent();
        //heartbeat.setAction("com.liutianjiao.smartvideosurveillance.HeartbeatService");
       // startService(heartbeat);
        add = (Button)findViewById(R.id.add);
        add.setOnClickListener(new functionOncClickListener());
        map = (TextView) findViewById(R.id.map);
        mapDrawable = getResources().getDrawable(R.drawable.map_pressed);
        map.setTextColor(Color.rgb(0x66, 0x99, 0xff));
        map.setCompoundDrawablesWithIntrinsicBounds(null, mapDrawable, null, null);
        unusual = (TextView) findViewById(R.id.unusual);
        map.setOnClickListener(new functionOncClickListener());
        unusual.setOnClickListener(new functionOncClickListener());
        unusualFragment = new UnusualFragment();
        mapFragment = new MapFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mapFragment);
        transaction.commit();
    }

    private class functionOncClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.map){
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, mapFragment);
                transaction.commit();
                map.setTextColor(Color.rgb(0x66, 0x99, 0xff));
                mapDrawable = getResources().getDrawable(R.drawable.map_pressed);
                map.setCompoundDrawablesWithIntrinsicBounds(null, mapDrawable, null, null);
                unusualDrawable = getResources().getDrawable(R.drawable.unusual_normal);
                unusual.setTextColor(Color.rgb(0xa9,0xa9, 0xa9));
                unusual.setCompoundDrawablesWithIntrinsicBounds(null, unusualDrawable, null, null);
            } else if(v.getId()==R.id.unusual){
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, unusualFragment);
                transaction.commit();
                map.setTextColor(Color.rgb(0xa9,0xa9, 0xa9));
                mapDrawable = getResources().getDrawable(R.drawable.map_normal);
                map.setCompoundDrawablesWithIntrinsicBounds(null, mapDrawable, null, null);
                unusualDrawable = getResources().getDrawable(R.drawable.unusual_pressed);
                unusual.setTextColor(Color.rgb(0x66,0x99,0xff));
                unusual.setCompoundDrawablesWithIntrinsicBounds(null, unusualDrawable, null, null);
            }else if(v.getId() == R.id.add){
                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                startActivity(intent);
            }

        }
    }
}
