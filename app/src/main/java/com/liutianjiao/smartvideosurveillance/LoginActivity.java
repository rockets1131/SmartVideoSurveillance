package com.liutianjiao.smartvideosurveillance;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liutianjiao.smartvideosurveillance.base.Config;
import com.liutianjiao.smartvideosurveillance.data.MSDBHelper;
import com.liutianjiao.smartvideosurveillance.data.OnlineIdentification;
import com.liutianjiao.smartvideosurveillance.data.SingleRequestQueue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends ActionBarActivity {
    private MSDBHelper msdbHelper;
    private SQLiteDatabase db;
    private Button buttonLogin;
    private TextView tips;
    private String userName;
    private String password;
    private static int IS_FINISH = 1;
    private RequestQueue rQueue;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        buttonLogin = (Button) findViewById(R.id.logIn);

        buttonLogin.setOnClickListener(new LoginListener());
        context = getBaseContext();
        rQueue = SingleRequestQueue.getRequestQueue(context);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen())
            db.close();
    }

    class LoginListener implements OnClickListener {
        public void onClick(View v) {
            EditText textUserName = (EditText) findViewById(R.id.userName);
            EditText textPassword = (EditText) findViewById(R.id.passWord);
            userName = textUserName.getText().toString();
            password = textPassword.getText().toString();
            buttonLogin.setBackgroundResource(R.drawable.press_button);
            buttonLogin.setText("登  录  中");
            buttonLogin.setClickable(false);
            tips = (TextView) findViewById(R.id.tips);
            tips.setText("");
            if (!userName.isEmpty()) {
                if (!password.isEmpty()) {
                    String url = Config.WEB_ADDRESS + "user.php?user_name="+userName+"&password="+password;
                    StringRequest stringRequest = new StringRequest(url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    buttonLogin.setBackgroundResource(R.drawable.button_radius);
                                    buttonLogin.setText(R.string.login);
                                    buttonLogin.setClickable(true);
                                    if (response.equals("correct")) {
                                        msdbHelper = new MSDBHelper(context, "MSDatabase.db3", 1);
                                        db = msdbHelper.getWritableDatabase();
                                        String QUERY_USER = "select password from usertable where username=?;";
                                        Date currentTime = new Date();
                                        Config.USER_NAME = userName;
                                        SimpleDateFormat formatter = new SimpleDateFormat(
                                                "yyyy-MM-dd HH:mm:ss");
                                        String dateString = formatter.format(currentTime);
                                        ContentValues values = new ContentValues();
                                        Cursor cursor = db.rawQuery(QUERY_USER,
                                                new String[]{userName});
                                        if (cursor.moveToFirst() == true) {
                                            if (password == cursor.getString(0)) {
                                                values.put("logintime", dateString);
                                                db.update("usertable", values, "username=?",
                                                        new String[]{userName});
                                            } else {
                                                values.put("logintime", dateString);
                                                values.put("password", password);
                                                db.update("usertable", values, "username=?",
                                                        new String[]{userName});
                                            }
                                        } else {
                                            values.put("username", userName);
                                            values.put("password", password);
                                            values.put("logintime", dateString);
                                            db.insert("usertable", null, values);
                                        }
                                        Intent intent = new Intent(getApplicationContext(),
                                                MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        String errorTips;
                                        switch (response) {
                                            case "user_name_error":
                                                errorTips = "用户名错误，请重新输入";
                                                break;
                                            case "password_error":
                                                errorTips = "密码错误，请重新输入";
                                                break;
                                            case "network_error":
                                                errorTips = "网络连接错误，错误代码network_error";
                                                break;
                                            default:
                                                errorTips = "系统错误，错误代码system_error";
                                                break;
                                        }
                                        tips.setText(errorTips);
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Config.NETWORK_STATUS = Config.NETWORK_ERROR;
                            buttonLogin.setBackgroundResource(R.drawable.button_radius);
                            buttonLogin.setText(R.string.login);
                            buttonLogin.setClickable(true);
                            tips.setText("网络连接错误或系统错误");
                            finish();
                        }
                    })/* {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("user_name", userName);
                            params.put("password", password);
                            return params;
                        }
                    }*/;
                    rQueue.add(stringRequest);
                } else
                    tips.setText("请输入密码");
            } else
                tips.setText("请输入用户名");
        }
    }
}