package com.bruh.chatapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "MessageStore";
    public static String COLUMN_NAME1 = "MessageColumn";
    public static String col_1 = "Message";
    public static String col_2 = "Kunci";
    public static String COLUMN_NAME2 = "UserLogin";
    public static String col_3 = "Username";
    public static String col_5 = "Private";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + COLUMN_NAME1 + " (Message TEXT,Kunci TEXT)");
        sqLiteDatabase.execSQL("create table " + COLUMN_NAME2 + " (Username TEXT,Private TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + COLUMN_NAME1);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + COLUMN_NAME2);
        onCreate(sqLiteDatabase);
    }

    public  boolean insertDataChat(String Message, String Key){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_1,Message);
        contentValues.put(col_2,Key);
        long result = db.insertOrThrow(COLUMN_NAME1,null,contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean insertDatalogin(String User,  String Privat){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_3,User);
        contentValues.put(col_5,Privat);
        long result = db.insertOrThrow(COLUMN_NAME2,null,contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean checkUid(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("Select * from " + COLUMN_NAME2 + " where " + col_3 + " = '" + uid +"'", null);

        if(result.getCount() <= 0){
            result.close();
            return  false;
        }
        result.close();
        return true;
    }

    public String getUidKey(String uid){
        String resultString = "not found";
        SQLiteDatabase db = this.getWritableDatabase();
        String whereas = "Username=?";
        String[] whereargs = new String[]{uid};
        Cursor result = db.query(COLUMN_NAME2,null,whereas,whereargs,null,null,null);
        if(result.moveToFirst()){
            resultString = result.getString(result.getColumnIndex(col_5));
        }

        return resultString;
    }

}
