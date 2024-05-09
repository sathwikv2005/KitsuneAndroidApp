package com.example.kitsune;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AttendanceDB extends SQLiteOpenHelper {
    public AttendanceDB(Context context) {
        super(context, "Attendance.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Attendance(course TEXT primary key,total_classes INTEGER,present INTEGER )");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists Attendance");
    }

    public boolean addCourse(String course,int total_classes, int present, boolean lab){
        if(lab) course = course.toUpperCase() + " (LAB)";
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("course", course.toUpperCase());
        contentValues.put("total_classes", total_classes);
        contentValues.put("present", present);
        long result = db.insert("Attendance",null,contentValues);
        return result != -1;
    }

    /**
     *
     * @param course course title
     * @param p true if attended class, false if absent
     *
     */
    public void present(String course,boolean p){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(p) contentValues.put("present", "present + 1");
        contentValues.put("total_classes", "total_classes + 1");
        db.update("Attendance", contentValues, "course = ?", new String[] {course.toUpperCase()});
    }

    public void update(String course,int p, int tc){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("present", p);
        contentValues.put("total_classes", tc);
        db.update("Attendance", contentValues, "course = ?", new String[] {course.toUpperCase()});
    }

    public String[] getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from Attendance",null);
        int length = cursor.getCount();
        String[] courses = new String[length];
        int i = 0;
        while(cursor.moveToNext()){
            courses[i++] = cursor.getString(0)+"-"+cursor.getInt(1)+"-"+cursor.getInt(2);
        }
        return courses;
    }


    public boolean deleteCourse(String course) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Attendance", "course=?", new String[]{course.toUpperCase()}) > 0;
    }

}
