package com.example.kitsune;

import android.app.PendingIntent;
import android.content.Context;
import android.app.AlarmManager;
import android.util.Log;

public class StartAlarmManager {
    private AlarmManager alarmManager;

    public StartAlarmManager(Context context) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }
    public void setAlarm(long triggerAt, PendingIntent pi){
        Log.d("KitsuneLog","Alarm manager set");
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+triggerAt,pi);
    }
}
