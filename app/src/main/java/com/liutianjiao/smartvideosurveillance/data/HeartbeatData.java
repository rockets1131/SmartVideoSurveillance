package com.liutianjiao.smartvideosurveillance.data;

import com.liutianjiao.smartvideosurveillance.base.BaseOnlineData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2014/12/18.
 */
public class HeartbeatData extends BaseOnlineData {
    private int netStatus;
    private List<NotificationData> notificationList = new ArrayList<NotificationData>();

    public HeartbeatData(String urlAdress) {
        super(urlAdress);
        if (jsonResult != null || !jsonResult.equals("network_error"))
            TranslateResult(jsonResult);
    }

    @Override
    protected void TranslateResult(String jsonResult) {
        try {
            JSONArray notiList = new JSONObject(jsonResult).getJSONArray("result");
            for (int i = 0; i < notiList.length(); i++) {
                NotificationData curNoti = new NotificationData();
                JSONObject tempNoti = (JSONObject) notiList.get(i);
                curNoti.notiCategory = tempNoti.getString("category");
                curNoti.notiSource = tempNoti.getString("source");
                curNoti.notiNum = tempNoti.getInt("num");
                notificationList.add(curNoti);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<NotificationData> GetResult() {
        return notificationList;
    }
}
