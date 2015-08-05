package com.liutianjiao.smartvideosurveillance.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class UnusualData {

    final String QUERY_UNUSUAL1 = "select unusualid,createtime,username,content,picturepath,picturename from unusualtable  order by createtime desc limit ?;";
    final String QUERY_UNUSUAL2 = "select unusualid,createtime,username,content,picturepath,picturename from unusualtable where unusualid < ? order by createtime desc limit ?";
    final String QUERY_UNUSUAL_EXIST = "select * from unusualtable where unusualid = ?;";
    private SQLiteDatabase db;
    private MSDBHelper msdbHelper;
    private Context context;

    public UnusualData(Context context) {
        this.context = context;
    }

    public ArrayList<Unusual> connDBForResult(int startPos, int offset) {
        msdbHelper = new MSDBHelper(context, "MSDatabase.db3", 1);
        db = msdbHelper.getReadableDatabase();
        ArrayList<Unusual> unusualList = new ArrayList<Unusual>();
        Cursor cursor;
        if (startPos < 0)
            cursor = db.rawQuery(QUERY_UNUSUAL1,
                    new String[]{String.valueOf(offset)});
        else
            cursor = db.rawQuery(
                    QUERY_UNUSUAL2,
                    new String[]{String.valueOf(startPos),
                            String.valueOf(offset)});
        if (cursor.moveToFirst()) {
            do {
                Unusual newUnusual = new Unusual();
                newUnusual.unusualId = cursor.getInt(0);
                newUnusual.unusualTime = cursor.getString(1);
                newUnusual.userName = cursor.getString(2);
                int contentIndex = cursor.getColumnIndex("content");
                //if (contentIndex != -1)
                    newUnusual.content = cursor.getString(contentIndex);
                int picIndex = cursor.getColumnIndex("picturepath");
                //if (picIndex != -1) {
                    newUnusual.picPath = cursor.getString(picIndex);
                if(newUnusual.picPath != null){
                    String tempName = cursor.getString(cursor.getColumnIndex("picturename"));
                    newUnusual.picFiles = tempName.split(";");//获取图片文件名
                }
                unusualList.add(newUnusual);
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (db != null && db.isOpen())
            db.close();
        return unusualList;
    }

    public void updateDBResult(ArrayList<Unusual> unusualList) {
        msdbHelper = new MSDBHelper(context, "MSDatabase.db3", 1);
        db = msdbHelper.getWritableDatabase();
        Cursor cursor = null;
        for (int i = 0; i < unusualList.size(); i++) {
            cursor = db
                    .rawQuery(QUERY_UNUSUAL_EXIST, new String[]{String
                            .valueOf(unusualList.get(i).unusualId)});
            if (!cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put("unusualid", unusualList.get(i).unusualId);
                values.put("createtime", unusualList.get(i).unusualTime);
                values.put("username", unusualList.get(i).userName);
                if (unusualList.get(i).content != null)
                    values.put("content", unusualList.get(i).content);
                if (unusualList.get(i).picPath != null) {
                    values.put("picturepath", unusualList.get(i).picPath);
                    String tempName = unusualList.get(i).picFiles[0];
                    int n = unusualList.get(i).picFiles.length;
                    for(int j = 1;j<n;j++)
                        tempName = tempName+";"+unusualList.get(i).picFiles[j];
                    values.put("picturename", tempName);
                }
                db.insert("unusualtable", null, values);

            }
        }
        if(cursor != null)
            cursor.close();
        if (db != null && db.isOpen())
            db.close();
    }
}
