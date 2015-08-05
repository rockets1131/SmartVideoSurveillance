package com.liutianjiao.smartvideosurveillance;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.liutianjiao.smartvideosurveillance.adapter.DeviceAdapter;
import com.liutianjiao.smartvideosurveillance.base.Config;
import com.liutianjiao.smartvideosurveillance.data.Device;
import com.liutianjiao.smartvideosurveillance.data.DeviceData;
import com.liutianjiao.smartvideosurveillance.data.Stage;


public class VideoActivity extends ActionBarActivity {

    private Context context;
    private TextView videoHint;
    private VideoView videoView;
    private String deviceIp;
    private Stage[] deviceList = null;
    private int deviceId;
    private Device playingDevice;
    private TextView deviceName;
    private ExpandableListView deviceListView;
    private DeviceData deviceData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        // 设置用于发广播的上下文
        if (getParent() != null)
            context = getParent();
        else
            context = this;
        Intent intent = getIntent();
        deviceId = intent.getIntExtra("deviceId", -1);
        deviceIp = intent.getStringExtra("deviceIp");
        deviceName = (TextView) findViewById(R.id.playing_device_name);
        deviceName.setText(intent.getStringExtra("deviceName"));
        videoHint = (TextView) findViewById(R.id.video_hint);
        videoView = (VideoView) findViewById(R.id.video_view);
        LayoutParams videoParams = new LinearLayout.LayoutParams(Config.DISPLAY_WIDTH, Config.DISPLAY_WIDTH * 720 / 1280 + 1);
        videoView.setLayoutParams(videoParams);
        if(Config.NETWORK_STATUS == Config.NETWORK_ERROR)
            Toast.makeText(context, "网络未连接，无法播放视频。", Toast.LENGTH_SHORT).show();
        else {
            videoView.setVideoURI(Uri.parse("rtsp://admin:632911632@" + deviceIp
                    + "/h264/ch1/main/av_stream"));
            videoView.setOnTouchListener(new videoOnTouchListener());
            videoView.setOnPreparedListener(new videoPreparedListener());
        }
        deviceData = Config.DEVICE_DATA;
        deviceList = deviceData.GetResult();
        deviceListView = (ExpandableListView) findViewById(R.id.device_list);
        deviceListView.setAdapter(new DeviceAdapter(context, deviceList));
        playingDevice = deviceData.getDevice(deviceId);
        deviceListView
                .setOnChildClickListener(new deviceOnChildClickListener());
        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new backClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoHint.setText("正在加载视频");
        videoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.stopPlayback();
    }

    private class backClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class deviceOnChildClickListener implements OnChildClickListener {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {
            if(Config.NETWORK_STATUS == Config.NETWORK_ERROR)
                Toast.makeText(context, "网络未连接，无法播放视频。", Toast.LENGTH_SHORT).show();
            else if (deviceId != id) {
                playingDevice = deviceData.getDevice((int) id);
                deviceIp = playingDevice.deviceIp;
                deviceName.setText(playingDevice.deviceName);
                deviceId = (int) id;
                videoView.stopPlayback();
                videoView.setVideoURI(Uri.parse("rtsp://admin:632911632@"
                        + deviceIp + ":554/PSIA/streaming/channels/101"));
                videoHint.setText("正在加载视频");
                videoView.setOnErrorListener(new videoErrorListener());
                videoView.start();
            }
            return false;
        }
    }

    private class videoOnTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            videoView.stopPlayback();
            Intent intent = new Intent(context, VideoFSActivity.class);
            intent.putExtra("deviceName", playingDevice.deviceName);
            intent.putExtra("deviceIp", deviceIp);
            startActivity(intent);
            return false;
        }
    }

    public class videoPreparedListener implements OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoHint.setText("正在播放");
        }
    }

    public class videoErrorListener implements OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            videoHint.setText("设备无法播放");
            return false;
        }

    }
}
