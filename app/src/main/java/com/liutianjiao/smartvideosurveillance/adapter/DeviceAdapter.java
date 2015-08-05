package com.liutianjiao.smartvideosurveillance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liutianjiao.smartvideosurveillance.R;
import com.liutianjiao.smartvideosurveillance.data.Device;
import com.liutianjiao.smartvideosurveillance.data.Stage;

import java.util.HashMap;
import java.util.Map;

public class DeviceAdapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater;
    private Stage[] deviceList = null;
    private Context context;
    private Map<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
    private int childrenCount[];

    public DeviceAdapter(Context context, Stage[] deviceList) {
        super();
        this.deviceList = deviceList;
        this.context = context;
        childrenCount = new int[deviceList.length];
        initHashMap();
    }

    @Override
    public Device getChild(int groupPosition, int childPosition) {
        int curPos = hashMap.get(groupPosition * 1000 + childPosition);
        int x = curPos / 1000, y = curPos % 1000 / 100, z = curPos % 100;
        return deviceList[x].nodeList[y].deviceList[z];
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        int curPos = hashMap.get(groupPosition * 1000 + childPosition);
        int x = curPos / 1000, y = curPos % 1000 / 100, z = curPos % 100;
        return deviceList[x].nodeList[y].deviceList[z].deviceId;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        int curPos = hashMap.get(groupPosition * 1000 + childPosition);
        int x = curPos / 1000, y = curPos % 1000 / 100, z = curPos % 100;
        RelativeLayout rLayout = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.device_style, null);

        TextView deviceName = (TextView) rLayout.findViewById(R.id.child_device_name);
        deviceName.setText(deviceList[x].nodeList[y].deviceList[z].deviceName);
        TextView process = (TextView) rLayout.findViewById(R.id.child_process);
        process.setText(deviceList[x].nodeList[y].deviceList[z].process);
        TextView place = (TextView) rLayout.findViewById(R.id.child_place);
        place.setText(deviceList[x].nodeList[y].deviceList[z].place);
        return rLayout;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childrenCount[groupPosition];
    }

    @Override
    public Stage getGroup(int groupPosition) {
        return deviceList[groupPosition];
    }

    @Override
    public int getGroupCount() {
        return deviceList.length;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        TextView stageName = new TextView(context);
        stageName.setText(deviceList[groupPosition].stageName);
        stageName.setPadding(50, 10, 0, 10);
        stageName.setTextSize(20);
        return stageName;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void initHashMap() {
        for (int i = 0; i < deviceList.length; i++) {
            int count = 0;
            for (int j = 0; j < deviceList[i].nodeList.length; j++) {
                for (int k = 0; k < deviceList[i].nodeList[j].deviceList.length; k++) {
                    if (deviceList[i].nodeList[j].deviceList[k].type
                            .equals("hik")) {
                        hashMap.put(i * 1000 + count, i * 1000 + j * 100 + k);
                        count++;
                    }
                }
            }
            childrenCount[i] = count;
        }
    }
}
