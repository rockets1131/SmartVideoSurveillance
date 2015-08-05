package com.liutianjiao.smartvideosurveillance.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MSDBHelper extends SQLiteOpenHelper {

    final String CREATE_USER_TABLE = "create table if not exists usertable(username varchar(50) primary key, password varchar(50), logintime datetime);";
    final String CREATE_DEVICE_TABLE = "create table if not exists devicetable(deviceid int(11) primary key, type varchar(255), devicename varchar(255), place varchar(255), nodeid int(11), deviceip varchar(255), process varchar(255), channel int(11), px int(11), py int(11));";
    final String CREATE_TERMINAL_TABLE = "create table if not exists terminaltable(nodeid int(11) primary key, nodename varchar(255), stageid int(11));";
    final String CREATE_STAGE_TABLE = "create table if not exists stagetable(stageid int(11) primary key, stagename varchar(255));";
    final String CREATE_EVENT_TABLE = "create table if not exists eventtable(eventid int(11) primary key, createtime datetime, type varchar(255), event varchar(255), deviceid int(11));";
    final String CREATE_UNUSUAL_TABLE = "create table if not exists unusualtable(unusualid int(11) primary key, createtime datetime, content varchar(255), username varchar(255), picturepath varchar(255), picturename varchar(255));";

    public MSDBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("myDB", "==onCreate==");
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_DEVICE_TABLE);
        db.execSQL(CREATE_TERMINAL_TABLE);
        db.execSQL(CREATE_STAGE_TABLE);
        db.execSQL(CREATE_EVENT_TABLE);
        db.execSQL(CREATE_UNUSUAL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
