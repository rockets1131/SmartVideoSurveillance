package com.liutianjiao.smartvideosurveillance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.liutianjiao.smartvideosurveillance.adapter.ScanPicAdapter;
import com.liutianjiao.smartvideosurveillance.base.Config;

import java.util.ArrayList;
import java.util.List;

public class PicScanActivity extends Activity {
    private List<String> pathList = new ArrayList<String>();
    private ViewPager viewPager;
    private Context context;
    private int currentPosition;
    private List<ImageView> imageList = new ArrayList<ImageView>();
    private TextView numberInfo;
    private Button deleteButton;
    private Button completeButton;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pic_scan);
        if (getParent() != null)
            context = getParent();
        else
            context = this;
        intent = getIntent();
        pathList = intent.getStringArrayListExtra("pictureList");
        currentPosition = intent.getIntExtra("curPosition", -1);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        numberInfo = (TextView) findViewById(R.id.pic_num);
        initViewPager();
        viewPager.setAdapter(new ScanPicAdapter(imageList));
        viewPager.setOnPageChangeListener(new imageOnChangeListener());
        viewPager.setCurrentItem(currentPosition);
        numberInfo.setText(String.valueOf(currentPosition + 1) + "/"
                + String.valueOf(pathList.size()));
        deleteButton = (Button) findViewById(R.id.delete);
        deleteButton.setOnClickListener(new functionButtonListener());
        completeButton = (Button) findViewById(R.id.complete);
        completeButton.setOnClickListener(new functionButtonListener());
    }

    private void initViewPager() {
        for (int i = 0; i < pathList.size(); i++) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            ImageView imageView = new ImageView(context);
            Bitmap bmp = BitmapFactory.decodeFile(pathList.get(i), options);
            if (options.outWidth > Config.DISPLAY_WIDTH
                    || options.outHeight > Config.DISPLAY_HEIGHT) {
                if (options.outWidth / Config.DISPLAY_WIDTH > options.outHeight
                        / Config.DISPLAY_HEIGHT)
                    options.inSampleSize = options.outWidth
                            / Config.DISPLAY_WIDTH;
                else
                    options.inSampleSize = options.outHeight
                            / Config.DISPLAY_HEIGHT;
                options.inJustDecodeBounds = false;

                bmp = BitmapFactory.decodeFile(pathList.get(i), options);
                imageView.setImageBitmap(bmp);
            } else {
                Uri uri = Uri.parse(pathList.get(i));
                imageView.setImageURI(uri);
            }
            imageList.add(imageView);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            intent.putStringArrayListExtra("pictureList",
                    (ArrayList<String>) pathList);
            setResult(1, intent);
            finish();// 传回参数
        }
        return false;
    }

    private class imageOnChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            currentPosition = arg0;
            numberInfo.setText(String.valueOf(currentPosition + 1) + "/"
                    + String.valueOf(pathList.size()));
        }

    }


    private class functionButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.delete) {
                pathList.remove(currentPosition);
                imageList.remove(currentPosition);
                if (pathList.size() == 0) {
                    intent.putStringArrayListExtra("pictureList",
                            (ArrayList<String>) pathList);
                    setResult(1, intent);
                    finish();// 传回参数
                } else {
                    if (currentPosition == pathList.size())// 删除最后一项，currentPosition提前
                        currentPosition--;
                    viewPager.getAdapter().notifyDataSetChanged();

                }
                numberInfo.setText(String.valueOf(currentPosition + 1) + "/"
                        + String.valueOf(pathList.size()));
            } else if (v.getId() == R.id.complete) {
                intent.putStringArrayListExtra("pictureList",
                        (ArrayList<String>) pathList);
                setResult(1, intent);
                finish();// 传回参数
            }
        }
    }
}
