package com.example.kitsune;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

class Slot{
    public String slot, course, venue;
    Slot(String slot,String course, String venue){
        this.slot = slot;
        this.course = course;
        this.venue = venue;
    }
}

public class DBManager extends SQLiteOpenHelper {
    public DBManager(Context context) {
        super(context, "TimeTable.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table TimeTable(slot TEXT primary key,course TEXT,venue TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists TimeTable");
    }

    public boolean addSlot(String slot,String course,String venue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("slot", slot);
        contentValues.put("course", course);
        contentValues.put("venue", venue);
        long result = db.insert("TimeTable",null,contentValues);
        return result != -1;
    }

    public String[] getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from TimeTable",null);
        int length = cursor.getCount();
        String[] slots = new String[length];
        int i = 0;
        while(cursor.moveToNext()){
            slots[i++] = cursor.getString(0)+"-"+cursor.getString(1)+"-"+cursor.getString(2);
        }
        return slots;
    }


    public boolean deleteSlot(String slot) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("TimeTable", "slot=?", new String[]{slot}) > 0;
    }

}
