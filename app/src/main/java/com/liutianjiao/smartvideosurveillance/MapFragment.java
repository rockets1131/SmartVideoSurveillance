package com.liutianjiao.smartvideosurveillance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liutianjiao.smartvideosurveillance.base.Config;
import com.liutianjiao.smartvideosurveillance.data.DeviceData;
import com.liutianjiao.smartvideosurveillance.data.SingleRequestQueue;
import com.liutianjiao.smartvideosurveillance.data.Stage;

import java.lang.reflect.Type;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {
    private View view;
    private Context context;
    private Spinner spinner;
    private Stage[] stageList;
    private int curSelect = 0;
    private int curStage = 0;
    private String[] arrStage;
    private FrameLayout frameLayout;
    private Button logButton;
    private Button videoButton;
    private View oval;
    private RequestQueue rQueue;
    private DeviceData deviceData;
    private FrameLayout.LayoutParams ovalParams = new FrameLayout.LayoutParams(140, 90);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);

        if(stageList == null) {
            if(Config.NETWORK_STATUS == Config.NETWORK_SUCCESS) {
                StringRequest stringRequest = new StringRequest(Config.WEB_ADDRESS + "devicelist.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Config.NETWORK_STATUS = Config.NETWORK_SUCCESS;
                                Gson gson = new Gson();
                                Type listType = new TypeToken<Stage[]>() {
                                }.getType();
                                stageList = gson.fromJson(response, listType);
                                if (stageList != null) {
                                    setDefault(0);
                                    deviceData.updateDBResult(stageList);
                                    Config.DEVICE_DATA = deviceData;
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Config.NETWORK_STATUS = Config.NETWORK_ERROR;
                        stageList = deviceData.GetResult();
                        Config.DEVICE_DATA = deviceData;
                        if (stageList != null) {
                            setDefault(0);
                        } else {
                            frameLayout = (FrameLayout) view.findViewById(R.id.container);
                            frameLayout.setBackgroundResource(R.drawable.nodata);
                        }
                    }
                });
                rQueue.add(stringRequest);
            } else {
                stageList = deviceData.GetResult();
                Config.DEVICE_DATA = deviceData;
                if (stageList != null) {
                    setDefault(0);
                } else {
                    frameLayout = (FrameLayout) view.findViewById(R.id.container);
                    frameLayout.setBackgroundResource(R.drawable.nodata);
                }
            }
        } else {
            setDefault(curSelect);
        }
        return view;
    }

    private void setDefault(int curSelect) {
        frameLayout = (FrameLayout) view.findViewById(R.id.container);
        frameLayout.setBackgroundResource(R.drawable.map911);
        InitButton();
        Button firstButton = (Button) view.findViewById(curSelect);
        firstButton.setBackgroundResource(R.drawable.focus_true);
        logButton = (Button) view.findViewById(R.id.log);
        logButton.setOnClickListener(new functionButtonListener());
        videoButton = (Button) view.findViewById(R.id.video);
        videoButton.setOnClickListener(new functionButtonListener());
        ChangeButton(curSelect);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getBaseContext();
        rQueue = SingleRequestQueue.getRequestQueue(context);
        deviceData = new DeviceData(context);
    }

    private int GetX(int px) {
        float widthPercent = ((float) Config.DISPLAY_WIDTH) / 768;
        return (int) (px * widthPercent);
    }

    private int GetY(int py) {
        float heightPercent = ((float) Config.DISPLAY_HEIGHT) / 1184;
        return (int) (py * heightPercent);
    }

    private void ChangeButton(int status) {
        int x = status / 1000, y = status % 1000 / 100, z = status % 100;
        ovalParams.leftMargin = GetX(stageList[x].nodeList[y].deviceList[z].px) - 40;
        ovalParams.topMargin = GetY(stageList[x].nodeList[y].deviceList[z].py) + 30;
        oval.setLayoutParams(ovalParams);
        TextView deviceName = (TextView) view.findViewById(R.id.device_name);
        deviceName.setText(stageList[x].nodeList[y].deviceList[z].deviceName);
        TextView process = (TextView) view.findViewById(R.id.process);
        process.setText("[" + stageList[x].nodeList[y].deviceList[z].process
                + "]");
        TextView place = (TextView) view.findViewById(R.id.place);
        place.setText(stageList[x].nodeList[y].deviceList[z].place);
        TextView type = (TextView) view.findViewById(R.id.type);
        type.setText(stageList[x].nodeList[y].deviceList[z].type);
        TextView nodeName = (TextView) view.findViewById(R.id.node_name);
        nodeName.setText(stageList[x].nodeList[y].nodeName);
        if (stageList[x].nodeList[y].deviceList[z].type.equals("hik"))
            videoButton.setVisibility(View.VISIBLE);
        else
            videoButton.setVisibility(View.GONE);
    }

    private class deviceButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() != curSelect) {
                v.setBackgroundResource(R.drawable.focus_true);
                ChangeButton(v.getId());
                Button temp = (Button) view.findViewById(curSelect);
                temp.setBackgroundResource(R.drawable.focus_false);
                curSelect = v.getId();
            }
        }
    }
    private void InitButton() {
        FrameLayout layout = (FrameLayout) view.findViewById(R.id.container);
        oval = new View(context);
        oval.setBackgroundResource(R.drawable.oval);
        layout.addView(oval);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        arrStage = new String[stageList.length];
        for (int i = 0; i < stageList.length; i++) {
            arrStage[i] = new String(stageList[i].stageName);
            for (int j = 0; j < stageList[i].nodeList.length; j++) {
                for (int k = 0; k < stageList[i].nodeList[j].deviceList.length; k++) {
                    Button curButton = new Button(context);
                    curButton.setBackgroundResource(R.drawable.focus_false);
                    FrameLayout.LayoutParams imagebtn_params = new FrameLayout.LayoutParams(
                            60, 80);
                    imagebtn_params.leftMargin = GetX(stageList[i].nodeList[j].deviceList[k].px);
                    imagebtn_params.topMargin = GetY(stageList[i].nodeList[j].deviceList[k].py);
                    curButton.setId(i * 1000 + j * 100 + k);
                    curButton.setLayoutParams(imagebtn_params);
                    layout.addView(curButton);
                    curButton
                            .setOnClickListener(new deviceButtonListener());
                    if (i == curStage)
                        curButton.setVisibility(View.VISIBLE);
                    else
                        curButton.setVisibility(View.GONE);
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.spinner_board, arrStage) {
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {

                View adapterView = getActivity().getLayoutInflater().inflate(
                            R.layout.spinner_item, parent, false);
                TextView label = (TextView) adapterView
                        .findViewById(R.id.label);
                label.setText(getItem(position));
                ImageView icon = (ImageView) adapterView
                        .findViewById(R.id.icon);
                icon.setVisibility(spinner.getSelectedItemPosition() == position ? View.VISIBLE
                        : View.INVISIBLE);
                return adapterView;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new stageOnItemSelectedListener());
    }
    private class stageOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapter, View vview,
                                   int position, long id) {
            // 获取选择的项的值
            if (curStage != position) {
                for (int i = 0; i < stageList.length; i++) {
                    for (int j = 0; j < stageList[i].nodeList.length; j++) {
                        for (int k = 0; k < stageList[i].nodeList[j].deviceList.length; k++) {
                            int curId = i * 1000 + j * 100 + k;
                            Button curButton = (Button) view.findViewById(curId);
                            if (i == position)
                                curButton.setVisibility(View.VISIBLE);
                            else
                                curButton.setVisibility(View.GONE);
                        }
                    }
                }
                Button curButton = (Button) view.findViewById(curSelect);
                curButton.setBackgroundResource(R.drawable.focus_false);
                curStage = position;
                frameLayout = (FrameLayout) view.findViewById(R.id.container);

                if (curStage == 0) {
                    frameLayout.setBackgroundResource(R.drawable.map911);
                    Button firstButton = (Button) view.findViewById(0);
                    firstButton.setBackgroundResource(R.drawable.focus_true);
                    ChangeButton(0);
                    curSelect = 0;
                } else {
                    frameLayout.setBackgroundResource(R.drawable.map631);
                    Button firstButton = (Button) view.findViewById(1000);
                    firstButton.setBackgroundResource(R.drawable.focus_true);
                    ChangeButton(1000);
                    curSelect = 1000;
                }

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private class functionButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int x = curSelect / 1000, y = curSelect % 1000 / 100, z = curSelect % 100;
            if (v.getId() == R.id.log) {
                Intent intent = new Intent(context, LogActivity.class);
                intent.putExtra("deviceName",
                        stageList[x].nodeList[y].deviceList[z].deviceName);
                intent.putExtra("deviceId",
                        stageList[x].nodeList[y].deviceList[z].deviceId);
                startActivity(intent);
            } else if (v.getId() == R.id.video) {
                Intent intent = new Intent(context, VideoActivity.class);
                intent.putExtra("deviceId",
                        stageList[x].nodeList[y].deviceList[z].deviceId);
                intent.putExtra("deviceIp",
                        stageList[x].nodeList[y].deviceList[z].deviceIp);
                intent.putExtra("deviceName",
                        stageList[x].nodeList[y].deviceList[z].deviceName);
                startActivity(intent);
            }
        }
    }
}
