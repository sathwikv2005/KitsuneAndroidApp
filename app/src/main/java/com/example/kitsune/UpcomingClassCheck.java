package com.example.kitsune;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.LocalDateTime;

public class UpcomingClassCheck extends Worker {
    Context context;

    public UpcomingClassCheck(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.d("KitsuneLog","UpcomingClassCheck Ran");
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        if(hour < 7 || hour > 18) return Result.success(); //Outside class hours
        int min = now.getMinute();
        if(min < 30) return Result.success();
        SlotsManager slotsManager = new SlotsManager();
        int day = slotsManager.getDayOfWeek(); //1-> sunday, 7->saturday
        if(day < 3) return Result.success();


        day -= 3; //convert to index in slots array
        DBManager db = new DBManager(context);
        String[] enrolledSlots = db.getData();
        String[][] allSlots = slotsManager.getAllSlots();
        String[] slots = allSlots[day];
        hour -= 7; //Convert to slots array index
        String slot = slots[hour];
        if(slot.equalsIgnoreCase("LUNCH")) return Result.success();
        int i = -1;
        if (slot.contains("-")) {
            String[] multiSlots = slot.split("-"); // Check if multiple slots are present at the given hour.
            i = checkForSlot(enrolledSlots, multiSlots);
            if (i == -1) i = checkForSlot(enrolledSlots, multiSlots);
        } else {
            i = checkForSlot(enrolledSlots, slot);
        }
        String[] classNow;
        String course;
        String venue;
        if(i == -1) { // Check for lab slots
            String LabSlots = slotsManager.getLabSlots()[day][hour];
            i = checkForSlot(enrolledSlots,LabSlots);
        }
        if(i == -1) return Result.success();
        classNow = enrolledSlots[i].split("-");
        course = classNow[1];
        venue = classNow[2];
        if(classNow.length > 3) venue += "-" + classNow[3];
        if(classNow.length > 4) venue += "-" + classNow[4];


        /*
            Notification
         */
        String channelID = "ClassReminders";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), channelID);
        Intent ii = new Intent(context.getApplicationContext(),MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 10, ii, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Your "+course+" class at "+venue+" Begins shortly"); //detail mode is the "expanded" notification
        bigText.setBigContentTitle(course+" At "+venue);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.hu_tao); //notification icon
        mBuilder.setContentTitle(course+" At "+venue); //main title
        mBuilder.setContentText("Your "+course+" class at "+venue+" Begins shortly"); //main text when you "haven't expanded" the notification yet
        mBuilder.setPriority(NotificationManager.IMPORTANCE_MAX);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(channelID,
                "Class reminder",
                NotificationManager.IMPORTANCE_DEFAULT);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(channel);
        }

        if (mNotificationManager != null) {
            mNotificationManager.notify(10, mBuilder.build());
        }
        /*
            Notification
        */
        return Result.success();
    }

    private int checkForSlot(String[] enrolled,String slot){
            for(int i = 0; i < enrolled.length;i++){
                if(enrolled[i].split("-")[0].equalsIgnoreCase(slot)) return i;
            }
            return -1;
    }
    private int checkForSlot(String[] enrolled,String[] slot){
        for(int i = 0; i < enrolled.length;i++){
            if(enrolled[i].split("-")[0].equalsIgnoreCase(slot[0]) || enrolled[i].split("-")[0].equalsIgnoreCase(slot[1])) return i;
        }
        return -1;
    }



}
