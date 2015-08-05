package com.liutianjiao.smartvideosurveillance.data;

import com.liutianjiao.smartvideosurveillance.base.BaseOnlineData;

import org.json.JSONObject;

public class OnlineIdentification extends BaseOnlineData {
    private String finalResult;

    public OnlineIdentification(String urlAdress) {
        super(urlAdress);
        TranslateResult(jsonResult);
    }

    @Override
    protected void TranslateResult(String jsonResult) {
        if (jsonResult == null || jsonResult.equals("network_error"))
            finalResult = null;
        else {
            try {
                JSONObject curResult = new JSONObject(jsonResult);
                finalResult = curResult.getString("result");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String GetResult() {
        return finalResult;
    }
}
