package com.liutianjiao.smartvideosurveillance;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liutianjiao.smartvideosurveillance.base.Config;
import com.liutianjiao.smartvideosurveillance.data.MSDBHelper;
import com.liutianjiao.smartvideosurveillance.data.SingleRequestQueue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends ActionBarActivity {
    final String QUERY_LATEST_USER = "select username, password from usertable order by logintime desc limit 1;";
    private MSDBHelper msdbHelper;
    private SQLiteDatabase db;
    private String userName;
    private String password;
    private RequestQueue rQueue;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        context = getBaseContext();
        msdbHelper = new MSDBHelper(context, "MSDatabase.db3", 1);
        db = msdbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_LATEST_USER, null);
        rQueue = SingleRequestQueue.getRequestQueue(context);


        if (cursor.moveToFirst() == true) {
            userName = cursor.getString(0);
            password = cursor.getString(1);
            Config.USER_NAME = userName;
            String url = Config.WEB_ADDRESS + "user.php?user_name="+userName+"&password="+password;
            StringRequest stringRequest = new StringRequest(url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Config.NETWORK_STATUS = Config.NETWORK_SUCCESS;
                            Intent intentLogin = new Intent(context, LoginActivity.class);
                            Intent intentIn = new Intent(context, MainActivity.class);
                            if (response.equals("correct")) {
                                ContentValues values = new ContentValues();
                                intentIn.putExtra("status", response);
                                Date currentTime = new Date();
                                SimpleDateFormat formatter = new SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss");
                                String dateString = formatter.format(currentTime);
                                values.put("logintime", dateString);
                                db.update("usertable", values, "username=?",
                                        new String[]{userName});
                                startActivity(intentIn);
                            } else
                                startActivity(intentLogin);
                            finish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Config.NETWORK_STATUS = Config.NETWORK_ERROR;
                    Toast.makeText(context,
                            "网络错误:network_error", Toast.LENGTH_SHORT).show();
                    Intent intentIn = new Intent(context, MainActivity.class);
                    startActivity(intentIn);
                    finish();
                }
            })/*{
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_name", userName);
                    params.put("password", password);
                    return params;
                }
            }*/;
            rQueue.add(stringRequest);
        } else {
            Intent intentLogin = new Intent(context, LoginActivity.class);
            startActivity(intentLogin);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen())
            db.close();
    }
}