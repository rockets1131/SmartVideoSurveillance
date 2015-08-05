package com.liutianjiao.smartvideosurveillance.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liutianjiao.smartvideosurveillance.base.BaseDBData;

import java.util.HashMap;
import java.util.Map;

public class DeviceData {
    final String QUERY_NODE = "select nodeid, nodename from terminaltable where stageid = ?;";
    final String QUERY_DEVICE = "select deviceid, type, devicename, place, deviceip, process, channel, px, py from devicetable where nodeid = ?;";
    final String QUERY_STAGE = "select stageid, stagename from stagetable;";
    private Stage[] stageList;
    private SQLiteDatabase db;
    private MSDBHelper msdbHelper;
    private Context context;
    private Map<Integer, Integer> idMap = new HashMap<Integer, Integer>();

    public DeviceData(Context context) {
        this.context = context;
        msdbHelper = new MSDBHelper(context, "MSDatabase.db3", 1);
        db = msdbHelper.getWritableDatabase();

    }

    private void initIdMap(Stage[] stageList) {
        for (int i = 0; i < stageList.length; i++) {
            for (int j = 0; j < stageList[i].nodeList.length; j++) {
                for (int k = 0; k < stageList[i].nodeList[j].deviceList.length; k++) {
                    idMap.put(stageList[i].nodeList[j].deviceList[k].deviceId,
                            i * 1000 + j * 100 + k);
                }
            }
        }
    }

    public Device getDevice(int deviceId) {
        int curPos = idMap.get(deviceId);
        int x = curPos / 1000, y = curPos % 1000 / 100, z = curPos % 100;
        return stageList[x].nodeList[y].deviceList[z];
    }

    public Stage[] GetResult() {
        if (stageList == null)
            connDBForResult();
        return stageList;
    }

    public void connDBForResult() {
        Cursor cursor = db.rawQuery(QUERY_STAGE, null);
        stageList = new Stage[cursor.getCount()];
        int i = 0, j = 0;
        if (cursor.moveToFirst() == true) {
            do {
                stageList[i] = new Stage();
                stageList[i].stageId = cursor.getInt(0);
                stageList[i].stageName = cursor.getString(1);
                i++;
            } while (cursor.moveToNext());
        }

        for (i = 0; i < stageList.length; i++) {
            cursor = db.rawQuery(QUERY_NODE,
                    new String[]{String.valueOf(stageList[i].stageId)});
            if (cursor.moveToFirst() == true) {
                stageList[i].nodeList = new Node[cursor.getCount()];
                j = 0;
                do {
                    stageList[i].nodeList[j] = new Node();
                    stageList[i].nodeList[j].nodeId = cursor.getInt(0);
                    stageList[i].nodeList[j].nodeName = cursor.getString(1);
                    j++;
                } while (cursor.moveToNext());
            }
        }

        for (i = 0; i < stageList.length; i++) {
            for (j = 0; j < stageList[i].nodeList.length; j++) {
                cursor = db.rawQuery(QUERY_DEVICE, new String[]{String
                        .valueOf(stageList[i].nodeList[j].nodeId)});
                if (cursor.moveToFirst() == true) {
                    stageList[i].nodeList[j].deviceList = new Device[cursor
                            .getCount()];
                    int k = 0;
                    do {
                        stageList[i].nodeList[j].deviceList[k] = new Device();
                        stageList[i].nodeList[j].deviceList[k].deviceId = cursor
                                .getInt(0);
                        stageList[i].nodeList[j].deviceList[k].type = cursor
                                .getString(1);
                        stageList[i].nodeList[j].deviceList[k].deviceName = cursor
                                .getString(2);
                        stageList[i].nodeList[j].deviceList[k].place = cursor
                                .getString(3);
                        stageList[i].nodeList[j].deviceList[k].deviceIp = cursor
                                .getString(4);
                        stageList[i].nodeList[j].deviceList[k].process = cursor
                                .getString(5);
                        stageList[i].nodeList[j].deviceList[k].channel = cursor
                                .getInt(6);
                        stageList[i].nodeList[j].deviceList[k].px = cursor
                                .getInt(7);
                        stageList[i].nodeList[j].deviceList[k].py = cursor
                                .getInt(8);
                        k++;
                    } while (cursor.moveToNext());
                }
            }
        }
        initIdMap(stageList);
        cursor.close();
    }

    public void updateDBResult(Stage[] newStageList) {
        this.stageList = newStageList;
        db.execSQL("delete from stagetable;");
        db.execSQL("delete from terminaltable;");
        db.execSQL("delete from devicetable;");
        for (int i = 0; i < stageList.length; i++) {
            ContentValues stageValues = new ContentValues();
            stageValues.put("stageid", stageList[i].stageId);
            stageValues.put("stagename", stageList[i].stageName);
            db.insert("stagetable", null, stageValues);
            for (int j = 0; j < stageList[i].nodeList.length; j++) {
                ContentValues nodeValues = new ContentValues();
                nodeValues.put("nodeid", stageList[i].nodeList[j].nodeId);
                nodeValues.put("nodename", stageList[i].nodeList[j].nodeName);
                nodeValues.put("stageid", stageList[i].stageId);
                db.insert("terminaltable", null, nodeValues);
                for (int k = 0; k < stageList[i].nodeList[j].deviceList.length; k++) {
                    ContentValues deviceValues = new ContentValues();
                    deviceValues.put("deviceid",
                            stageList[i].nodeList[j].deviceList[k].deviceId);
                    deviceValues.put("type",
                            stageList[i].nodeList[j].deviceList[k].type);
                    deviceValues.put("devicename",
                            stageList[i].nodeList[j].deviceList[k].deviceName);
                    deviceValues.put("place",
                            stageList[i].nodeList[j].deviceList[k].place);
                    deviceValues.put("deviceip",
                            stageList[i].nodeList[j].deviceList[k].deviceIp);
                    deviceValues.put("process",
                            stageList[i].nodeList[j].deviceList[k].process);
                    deviceValues.put("channel",
                            stageList[i].nodeList[j].deviceList[k].channel);
                    deviceValues.put("px",
                            stageList[i].nodeList[j].deviceList[k].px);
                    deviceValues.put("py",
                            stageList[i].nodeList[j].deviceList[k].py);
                    deviceValues
                            .put("nodeid", stageList[i].nodeList[j].nodeId);
                    db.insert("devicetable", null, deviceValues);
                }
            }
        }
        initIdMap(stageList);
    }
}
