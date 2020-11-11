package ru.wtw.moreliatalkclient;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_MORELIA = "morelia.db";

    public static final String TABLE_JSON_NAME = "json";
    public static final String COLUMN_JSON_ID = "id";
    public static final String COLUMN_JSON_DIRECTION = "direction";
    public static final String COLUMN_JSON_TEXT = "text";

    public static final String TABLE_FLOWS_NAME = "flows";
    public static final String COLUMN_FLOW_ID = "id";
    public static final String COLUMN_FLOW_SERVER_ID = "uuid";
    public static final String COLUMN_FLOW_TITLE = "title";
    public static final String COLUMN_FLOW_TYPE = "type";

    public static final String TABLE_USERS_NAME = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_SERVER_ID = "uuid";
    public static final String COLUMN_USER_NAME = "name";

    public static final String TABLE_MESSAGES_NAME = "flows";
    public static final String COLUMN_MESSAGE_ID = "id";
    public static final String COLUMN_MESSAGE_SERVER_ID = "uuid";
    public static final String COLUMN_MESSAGE_TIME = "time";
    public static final String COLUMN_MESSAGE_TEXT = "text";
    public static final String COLUMN_MESSAGE_FLOW_ID = "flow_id";
    public static final String COLUMN_MESSAGE_USER_ID = "user_id";


    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_MORELIA , null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_JSON_NAME + "(" +
                        COLUMN_JSON_ID + " integer primary key, " +
                        COLUMN_JSON_DIRECTION + " integer, " +
                        COLUMN_JSON_TEXT + " text )"
        );
        db.execSQL(
                "create table " + TABLE_FLOWS_NAME + "(" +
                        COLUMN_FLOW_ID + " integer primary key, " +
                        COLUMN_FLOW_SERVER_ID + " text, " +
                        COLUMN_FLOW_TITLE + " text, " +
                        COLUMN_FLOW_TYPE + " text )"
        );
        db.execSQL(
                "create table " + TABLE_USERS_NAME + "(" +
                        COLUMN_USER_ID + " integer primary key, " +
                        COLUMN_USER_SERVER_ID + " text, " +
                        COLUMN_USER_NAME + " text )"
        );
        db.execSQL(
                "create table " + TABLE_MESSAGES_NAME + "(" +
                        COLUMN_MESSAGE_ID + " integer primary key, " +
                        COLUMN_MESSAGE_SERVER_ID + " text, " +
                        COLUMN_MESSAGE_TIME + " datetime, " +
                        COLUMN_MESSAGE_TEXT + " text, " +
                        COLUMN_MESSAGE_FLOW_ID + " text, " +
                        COLUMN_MESSAGE_USER_ID + " text )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JSON_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLOWS_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES_NAME);
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

    public boolean clearJSON() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_JSON_NAME,null,null);
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

    public boolean insertFlow(String id, String title, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FLOW_SERVER_ID, id);
        contentValues.put(COLUMN_FLOW_TITLE, title);
        contentValues.put(COLUMN_FLOW_TYPE, type);

        int u = db.update(TABLE_FLOWS_NAME, contentValues, COLUMN_FLOW_SERVER_ID+"=?", new String[]{id});
        if (u == 0) {
            db.insertWithOnConflict(TABLE_FLOWS_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
/*
        db.insert(TABLE_FLOWS_NAME, null, contentValues);
*/
        return true;
    }

    public boolean insertMsg(String id, String flow_id, String user_id, String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MESSAGE_SERVER_ID, id);
        contentValues.put(COLUMN_MESSAGE_FLOW_ID, flow_id);
        contentValues.put(COLUMN_MESSAGE_USER_ID, user_id);
        contentValues.put(COLUMN_MESSAGE_TEXT, text);

        int u = db.update(TABLE_MESSAGES_NAME, contentValues, COLUMN_MESSAGE_SERVER_ID+"=?", new String[]{id});
        if (u == 0) {
            db.insertWithOnConflict(TABLE_MESSAGES_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
/*
        db.insert(TABLE_FLOWS_NAME, null, contentValues);
*/
        return true;
    }

    public ArrayList<String> getAllFlow() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_FLOWS_NAME, null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add("id: "+res.getString(res.getColumnIndex(COLUMN_FLOW_SERVER_ID))+
                    "    " + res.getString(res.getColumnIndex(COLUMN_FLOW_TITLE))+
                    " ("+ res.getString(res.getColumnIndex(COLUMN_FLOW_TYPE))+")");
            res.moveToNext();
        }

        return array_list;
    }

    public boolean clearFlows() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FLOWS_NAME,null,null);
        return true;
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

