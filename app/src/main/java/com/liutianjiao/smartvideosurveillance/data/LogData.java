package com.liutianjiao.smartvideosurveillance.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liutianjiao.smartvideosurveillance.base.BaseDBData;
import com.liutianjiao.smartvideosurveillance.base.BaseOnlineData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LogData {
    final String QUERY_LOG1 = "select eventid,createTime,type,event from eventtable where deviceid = ? order by createTime desc limit ?;";
    final String QUERY_LOG2 = "select eventid,createTime,type,event from eventtable where deviceid = ? and  eventid < ? order by createTime desc limit ?";
    final String QUERY_LOG_EXIST = "select * from eventtable where eventid = ?;";
    private SQLiteDatabase db;
    private MSDBHelper msdbHelper;
    private Context context;

    public LogData(Context context) {
        this.context = context;
    }

    public ArrayList<Log> connDBForResult(int deviceId, int startPos, int offset) {
        msdbHelper = new MSDBHelper(context, "MSDatabase.db3", 1);
        db = msdbHelper.getReadableDatabase();
        ArrayList<Log> logList = new ArrayList<Log>();

        Cursor cursor;
        if (startPos < 0)
            cursor = db.rawQuery(
                    QUERY_LOG1,
                    new String[]{String.valueOf(deviceId),
                            String.valueOf(offset)});
        else
            cursor = db.rawQuery(
                    QUERY_LOG2,
                    new String[]{String.valueOf(deviceId),
                            String.valueOf(startPos), String.valueOf(offset)});
        if (cursor.moveToFirst()) {
            do {
                Log newLog = new Log();
                newLog.eventId = cursor.getInt(0);
                newLog.logTime = cursor.getString(1);
                newLog.logType = cursor.getString(2);
                newLog.logEvent = cursor.getString(3);
                logList.add(newLog);
            } while (cursor.moveToNext());
        }
        if(cursor!=null)
            cursor.close();
        if (db != null && db.isOpen())
            db.close();
        return logList;
    }

    public void updateDBResult(ArrayList<Log> logList, int deviceId) {
        msdbHelper = new MSDBHelper(context, "MSDatabase.db3", 1);
        db = msdbHelper.getWritableDatabase();
        Cursor cursor = null;
        for (int i = 0; i < logList.size(); i++) {
            cursor = db.rawQuery(QUERY_LOG_EXIST,
                    new String[]{String.valueOf(logList.get(i).eventId)});
            if (!cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put("eventid", logList.get(i).eventId);
                values.put("createtime", logList.get(i).logTime);
                values.put("type", logList.get(i).logType);
                values.put("event", logList.get(i).logEvent);
                values.put("deviceid", deviceId);
                db.insert("eventtable", null, values);
            }
        }
        if(cursor!=null)
            cursor.close();
        if (db != null && db.isOpen())
            db.close();
    }
}
