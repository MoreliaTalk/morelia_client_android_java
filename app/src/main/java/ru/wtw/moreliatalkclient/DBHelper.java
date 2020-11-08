package ru.wtw.moreliatalkclient;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import ru.wtw.moreliatalkclient.ui.jsonlogs.JsonLogsFragment;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_JSON_NAME = "moreliajson.db";
    public static final String TABLE_JSON_NAME = "json";
    public static final String COLUMN_JSON_ID = "id";
    public static final String COLUMN_JSON_DIRECTION = "direction";
    public static final String COLUMN_JSON_TEXT = "text";

    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_JSON_NAME , null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " + TABLE_JSON_NAME + "(" + COLUMN_JSON_ID + " integer primary key, " +
                        COLUMN_JSON_DIRECTION + " integer, " + COLUMN_JSON_TEXT + " text )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JSON_NAME);
        onCreate(db);
    }

    public boolean insertJSON(String json) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_JSON_DIRECTION, 0);
        contentValues.put(COLUMN_JSON_TEXT, json);
        db.insert(TABLE_JSON_NAME, null, contentValues);
        return true;
    }

    public Cursor getJsonData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_JSON_NAME +
                " where " + COLUMN_JSON_ID + "=" + id + "", null);
        return res;
    }

    public int numberJsonRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_JSON_NAME);
        return numRows;
    }

    public ArrayList<String> getAllJson() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_JSON_NAME, null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(COLUMN_JSON_TEXT)));
            res.moveToNext();
        }

        return array_list;
    }
}

/*
    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
*/

