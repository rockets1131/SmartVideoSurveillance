package com.liutianjiao.smartvideosurveillance;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.VideoView;


public class VideoFSActivity extends ActionBarActivity {

    private TextView deviceName;
    private String deviceIp;
    private VideoView videoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_fs);
        Intent intent = getIntent();
        deviceName = (TextView) findViewById(R.id.device_name_fs);
        deviceName.setText(intent.getStringExtra("deviceName"));
        deviceIp = intent.getStringExtra("deviceIp");
        videoView = (VideoView) findViewById(R.id.video_view_fs);
        videoView.setVideoURI(Uri.parse("rtsp://admin:632911632@" + deviceIp
                + "/h264/ch1/main/av_stream"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.stopPlayback();
    }
}
