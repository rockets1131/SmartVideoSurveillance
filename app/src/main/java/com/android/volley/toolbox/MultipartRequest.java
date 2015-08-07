package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;

import org.apache.http.HttpEntity;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rockets1131 on 2015/8/7.
 */
public abstract class MultipartRequest extends Request<String> {

    private final Listener mListener;
    private HttpEntity mEntity;

    public MultipartRequest(String url, Listener listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);

        mListener = listener;
        mEntity = getHttpEntity();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HTTP.CHARSET_PARAM, HTTP.UTF_8);
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return mEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            mEntity.writeTo(baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public abstract HttpEntity getHttpEntity();

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            //parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}
