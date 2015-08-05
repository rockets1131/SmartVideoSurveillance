package com.liutianjiao.smartvideosurveillance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.liutianjiao.smartvideosurveillance.adapter.ScanPicAdapter;
import com.liutianjiao.smartvideosurveillance.base.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OriPicActivity extends ActionBarActivity {

    private String[] nameList;
    private String picPath;
    private ViewPager viewPager;
    private Context context;
    private int currentPosition;
    private TextView numberInfo;
    private Intent intent;
    private File ALBUM_PATH;
    private boolean[] isLocal;
    private List<ImageView> imageList = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ori_pic);
        if (getParent() != null)
            context = getParent();
        else
            context = this;
        intent = getIntent();
        nameList = intent.getStringArrayExtra("nameList");
        picPath = intent.getStringExtra("picPath");
        currentPosition = intent.getIntExtra("curPosition", -1);
        isLocal = new boolean[nameList.length];
        initViewPager();
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new ScanPicAdapter(imageList));
        numberInfo = (TextView) findViewById(R.id.pic_num);
    }

    private void initViewPager() {
        for (int i = 0; i < nameList.length; i++) {
            ALBUM_PATH = context.getExternalFilesDir(null);
            BitmapFactory.Options options = new BitmapFactory.Options();
            ImageView imageView = new ImageView(context);
            String curPath = ALBUM_PATH + picPath + nameList[i];
            File file = new File(curPath);
            if (file.exists()) {
                options.inJustDecodeBounds = true;
                Bitmap bmp = BitmapFactory.decodeFile(curPath, options);
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
                }
                bmp = BitmapFactory.decodeFile(curPath, options);
                imageView.setImageBitmap(bmp);
                isLocal[i] = true;
            } else {
                curPath = ALBUM_PATH.getAbsolutePath() + "/" + picPath + "small/"
                        + nameList[i];
                Bitmap bmp = BitmapFactory.decodeFile(curPath);
                imageView.setImageBitmap(bmp);
                isLocal[i] = false;
            }
            imageList.add(imageView);
        }
    }
}
