package com.liutianjiao.smartvideosurveillance.base;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public abstract class BaseOnlineData {
    protected String jsonResult;
    protected int networkStatus;

    public BaseOnlineData(String urlAdress) {
        connServerForResult(urlAdress);
    }

    private void connServerForResult(String urlAdress) {
        HttpResponse httpResponse = null;
        HttpGet httpGet = new HttpGet(urlAdress);
        HttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 3000);// �������ӳ�ʱ10��
        HttpConnectionParams.setSoTimeout(params, 3000); // ���ö�ȡ��ʱ10��
        String strResult = "";
        try {
            httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                strResult = EntityUtils.toString(httpResponse.getEntity());
                networkStatus = Config.NETWORK_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
            strResult = "network_error";
            networkStatus = Config.NETWORK_ERROR;
        }
        jsonResult = strResult;
    }

    abstract protected void TranslateResult(String jsonResult);

    abstract public Object GetResult();

    public int GetNetworkStatus() {
        return networkStatus;
    }
}