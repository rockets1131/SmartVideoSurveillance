package com.liutianjiao.smartvideosurveillance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liutianjiao.smartvideosurveillance.R;
import com.liutianjiao.smartvideosurveillance.data.Log;

import java.util.List;

public class LogAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<Log> logList;

    public LogAdapter(Context context, List<Log> logList) {
        super();
        this.context = context;
        this.logList = logList;
    }

    ;

    @Override
    public int getCount() {
        return logList.size();
    }

    @Override
    public Log getItem(int position) {
        return logList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return logList.get(position).eventId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout rLayout = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.log_style, null);
        TextView logType = (TextView) rLayout.findViewById(R.id.log_type);
        logType.setText(logList.get(position).logType);
        TextView logEvent = (TextView) rLayout.findViewById(R.id.event);
        logEvent.setText(logList.get(position).logEvent);
        TextView logTime = (TextView) rLayout.findViewById(R.id.log_time);
        logTime.setText(logList.get(position).logTime);
        return rLayout;
    }

}
