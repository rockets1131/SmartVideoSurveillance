package com.liutianjiao.smartvideosurveillance;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liutianjiao.smartvideosurveillance.adapter.LogAdapter;
import com.liutianjiao.smartvideosurveillance.base.Config;
import com.liutianjiao.smartvideosurveillance.data.Log;
import com.liutianjiao.smartvideosurveillance.data.LogData;
import com.liutianjiao.smartvideosurveillance.data.SingleRequestQueue;
import com.liutianjiao.smartvideosurveillance.widget.AutoListView;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class LogActivity extends ActionBarActivity {
    private RequestQueue rQueue;
    private Context context;
    private int deviceId;
    private AutoListView listView;
    private ArrayList<Log> logList;
    private int startPos = -1, offset = 20;
    private ImageView imageView;
    private LogAdapter logAdapter;
    private LogData logData;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        context = getBaseContext();
        String deviceName = null;
        Intent intent = getIntent();
        deviceName = intent.getStringExtra("deviceName");
        deviceId = intent.getIntExtra("deviceId", -1);
        TextView titleDevice = (TextView) findViewById(R.id.title_device);
        titleDevice.setText(deviceName);
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new backClickListener());
        rQueue = SingleRequestQueue.getRequestQueue(context);
        listView = (AutoListView)findViewById(R.id.log_list);
        imageView = (ImageView) findViewById(R.id.nodata);
        listView.setPageSize(offset);
        logData = new LogData(context);
        if(Config.NETWORK_STATUS == Config.NETWORK_SUCCESS) {
            String reqUrl = Config.WEB_ADDRESS + "messagelist.php?deviceid="
                    + String.valueOf(deviceId) + "&startid=" + String.valueOf(startPos) + "&offset=" + String.valueOf(offset);
            StringRequest stringRequest = new StringRequest(reqUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<ArrayList<Log>>() {
                            }.getType();
                            logList = gson.fromJson(response, listType);
                            logData.updateDBResult(logList, deviceId);
                            Config.NETWORK_STATUS = Config.NETWORK_SUCCESS;
                            initListView();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Config.NETWORK_STATUS = Config.NETWORK_ERROR;
                    logList = logData.connDBForResult(deviceId, startPos, offset);
                    initListView();
                }
            });
            rQueue.add(stringRequest);
        } else {
            logList = logData.connDBForResult(deviceId, startPos, offset);
            initListView();
        }
    }
    private void initListView() {
        if (logList.isEmpty()) {
            listView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        } else {
            logAdapter = new LogAdapter(context, logList);
            listView.setAdapter(logAdapter);
            listView.setResultSize(logList.size());
            startPos = logList.get(logList.size() - 1).eventId;
            listView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            listView.setOnLoadListener(new logListOnLoadListener());
            listView.setOnRefreshListener(new logListOnRefreshListener());
        }
    }

    private class logListOnRefreshListener implements AutoListView.OnRefreshListener {

        @Override
        public void onRefresh() {
            if(Config.NETWORK_STATUS == Config.NETWORK_ERROR) {
                new AsyncTask() {

                    @Override
                    protected Object doInBackground(Object[] params) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object params) {
                        listView.onRefreshComplete();
                        Toast.makeText(context, "网络未连接，无法刷新。", Toast.LENGTH_SHORT).show();
                    }

                }.execute();

            }
        }
    }

    private class logListOnLoadListener implements AutoListView.OnLoadListener {

        @Override
        public void onLoad() {
            //if(Config.NETWORK_STATUS == Config.NETWORK_SUCCESS) {
                String reqUrl = Config.WEB_ADDRESS + "messagelist.php?deviceid="
                        + String.valueOf(deviceId) + "&startid=" + String.valueOf(startPos) + "&offset=" + String.valueOf(offset);

                StringRequest stringRequest = new StringRequest(reqUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Config.NETWORK_STATUS = Config.NETWORK_SUCCESS;
                                Gson gson = new Gson();
                                Type listType = new TypeToken<ArrayList<Log>>() {
                                }.getType();
                                ArrayList<Log> curList = gson.fromJson(response, listType);
                                logList.addAll(curList);
                                listView.setResultSize(curList.size());
                                logData.updateDBResult(curList, deviceId);
                                loadListView();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Config.NETWORK_STATUS = Config.NETWORK_ERROR;
                        ArrayList<Log> curList = logData.connDBForResult(deviceId, startPos, offset);
                        Toast.makeText(context, "网络未连接", Toast.LENGTH_SHORT).show();
                        logList.addAll(curList);
                        listView.setResultSize(curList.size());
                        loadListView();
                    }
                }
                );
                rQueue.add(stringRequest);
            /*} else {
                ArrayList<Log> curList = logData.connDBForResult(deviceId, startPos, offset);
                logList.addAll(curList);
                listView.setResultSize(curList.size());
                loadListView();
            }*/
        }
    }

    private void loadListView() {
        logAdapter.notifyDataSetChanged();
        startPos = logList.get(logList.size() - 1).eventId;
        listView.onLoadComplete();
    }

    private class backClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
