package com.liutianjiao.smartvideosurveillance;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liutianjiao.smartvideosurveillance.adapter.UnusualAdapter;
import com.liutianjiao.smartvideosurveillance.base.Config;
import com.liutianjiao.smartvideosurveillance.data.SingleRequestQueue;
import com.liutianjiao.smartvideosurveillance.data.Unusual;
import com.liutianjiao.smartvideosurveillance.data.UnusualData;
import com.liutianjiao.smartvideosurveillance.widget.AutoListView;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class UnusualFragment extends Fragment {
    private RequestQueue rQueue;
    private Context context;
    private ArrayList<Unusual> unusualList, curList, newList;
    private AutoListView listView;
    private TextView userName;
    private UnusualAdapter unusualAdapter;
    private int startPos = -1, offset = 20;
    private View view;
    private ImageView imageView;
    private UnusualData unusualData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_unusual, container, false);
        userName = (TextView) view.findViewById(R.id.title_user);
        userName.setText(Config.USER_NAME);
        listView = (AutoListView) view.findViewById(R.id.unusual_list);
        imageView = (ImageView) view.findViewById(R.id.nodata);
        listView.setPageSize(offset);
        if (unusualList==null) {
            if (Config.NETWORK_STATUS == Config.NETWORK_SUCCESS) {
                String reqUrl = Config.WEB_ADDRESS + "unusuallist.php?startid=" + String.valueOf(startPos) + "&offset=" + String.valueOf(offset);
                StringRequest stringRequest = new StringRequest(reqUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Gson gson = new Gson();
                                Type listType = new TypeToken<ArrayList<Unusual>>() {
                                }.getType();
                                unusualList = gson.fromJson(response, listType);
                                unusualData.updateDBResult(unusualList);
                                Config.NETWORK_STATUS = Config.NETWORK_SUCCESS;
                                initListView();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Config.NETWORK_STATUS = Config.NETWORK_ERROR;
                        unusualList = unusualData.connDBForResult(startPos, offset);
                        initListView();
                    }
                });
                rQueue.add(stringRequest);
            } else {
                unusualList = unusualData.connDBForResult(startPos, offset);
                initListView();
            }
        } else {
            if (Config.NETWORK_STATUS == Config.NETWORK_SUCCESS) {
                String reqUrl = Config.WEB_ADDRESS + "unusuallist.php?startid=" + String.valueOf(unusualList.get(0).unusualId) + "&offset=" + String.valueOf(-1);
                StringRequest stringRequest = new StringRequest(reqUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Gson gson = new Gson();
                                Type listType = new TypeToken<ArrayList<Unusual>>() {
                                }.getType();
                                newList = gson.fromJson(response, listType);
                                unusualData.updateDBResult(newList);
                                unusualList.addAll(0, newList);
                                Config.NETWORK_STATUS = Config.NETWORK_SUCCESS;
                                //unusualAdapter.notifyDataSetChanged();
                                initListView();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Config.NETWORK_STATUS = Config.NETWORK_ERROR;
                        Toast.makeText(context, "网络未连接", Toast.LENGTH_SHORT).show();
                        initListView();
                    }
                });
                rQueue.add(stringRequest);
            } else
                initListView();
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getBaseContext();
        rQueue = SingleRequestQueue.getRequestQueue(context);
        unusualData = new UnusualData(context);
    }

    private void initListView() {
        if (unusualList.isEmpty()) {
            listView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        } else {
            unusualAdapter = new UnusualAdapter(context, unusualList);
            listView.setAdapter(unusualAdapter);
            listView.setResultSize(unusualList.size());
            startPos = unusualList.get(unusualList.size() - 1).unusualId;
            listView.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener(new unsusualClickListener());
            imageView.setVisibility(View.GONE);
            listView.setOnLoadListener(new unusualListOnLoadListener());
            listView.setOnRefreshListener(new unusualListOnRefreshListener());
        }
    }


    private class unusualListOnRefreshListener implements AutoListView.OnRefreshListener {

        @Override
        public void onRefresh() {
            if (Config.NETWORK_STATUS == Config.NETWORK_SUCCESS) {
                String reqUrl = Config.WEB_ADDRESS + "unusuallist.php?startid=" + String.valueOf(unusualList.get(0).unusualId) + "&offset=" + String.valueOf(-1);
                StringRequest stringRequest = new StringRequest(reqUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Gson gson = new Gson();
                                Type listType = new TypeToken<ArrayList<Unusual>>() {
                                }.getType();
                                newList = gson.fromJson(response, listType);
                                unusualData.updateDBResult(newList);
                                unusualList.addAll(0, newList);
                                if(newList.isEmpty())
                                    Toast.makeText(context, "没有新的异常事件", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(context, "刷新"+ String.valueOf(newList.size())+"条异常信息", Toast.LENGTH_SHORT).show();
                                Config.NETWORK_STATUS = Config.NETWORK_SUCCESS;
                                unusualAdapter.notifyDataSetChanged();
                                listView.onRefreshComplete();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Config.NETWORK_STATUS = Config.NETWORK_ERROR;
                        Toast.makeText(context, "网络未连接，无法刷新", Toast.LENGTH_SHORT).show();
                        listView.onRefreshComplete();
                    }
                });
                rQueue.add(stringRequest);
            }
        }
    }

    private class unusualListOnLoadListener implements AutoListView.OnLoadListener {

        @Override
        public void onLoad() {
            if(Config.NETWORK_STATUS != Config.NETWORK_SUCCESS) {
                curList = unusualData.connDBForResult(startPos, offset);
                loadListView();
            } else {
                String reqUrl = Config.WEB_ADDRESS + "unusuallist.php?startid=" + String.valueOf(startPos) + "&offset=" + String.valueOf(offset);
                StringRequest stringRequest = new StringRequest(reqUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Config.NETWORK_STATUS = Config.NETWORK_SUCCESS;
                                Gson gson = new Gson();
                                Type listType = new TypeToken<ArrayList<Unusual>>() {
                                }.getType();
                                curList = gson.fromJson(response, listType);
                                unusualData.updateDBResult(curList);
                                loadListView();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Config.NETWORK_STATUS = Config.NETWORK_ERROR;
                        Toast.makeText(context, "网络未连接，读取本地数据", Toast.LENGTH_SHORT).show();
                        curList = unusualData.connDBForResult(startPos, offset);
                        loadListView();
                    }
                }
                );
                rQueue.add(stringRequest);
            }
        }
    }

    private void loadListView() {
        unusualList.addAll(curList);
        listView.setResultSize(curList.size());
        unusualAdapter.notifyDataSetChanged();
        startPos = curList.get(curList.size() - 1).unusualId;
        curList.clear();
        listView.onLoadComplete();
    }

    private class unsusualClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            arg0.getAdapter().getItem(arg2);
        }
    }
}
