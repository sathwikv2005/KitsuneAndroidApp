package com.example.kitsune;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.work.ListenableWorker;

import java.time.LocalDateTime;

public class AttendanceAskNoti {
    Context context;
    public AttendanceAskNoti(Context context){
        this.context = context;
    }
    public void doWork(){
        Log.d("KitsuneLog","AttendanceAskNoti Ran");
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        if(hour < 8 || hour > 18) return; //Outside class hours
        int min = now.getMinute();
        if(min < 30 || min > 50) return;
        SlotsManager slotsManager = new SlotsManager();
        int day = slotsManager.getDayOfWeek(); //1-> sunday, 7->saturday
        if(day < 3) return;

        day -= 3; //convert to index in slots array
        DBManager db = new DBManager(context);
        String[] enrolledSlots = db.getData();
        String[][] allSlots = slotsManager.getAllSlots();
        String[] slots = allSlots[day];
        hour -= 8; //Convert to slots array index
        String slot = slots[hour];
        if(slot.equalsIgnoreCase("LUNCH")) return;
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
        boolean lab = false;
        if(i == -1) { // Check for lab slots
            lab = true;
            String LabSlots = slotsManager.getLabSlots()[day][hour];
            i = checkForSlot(enrolledSlots,LabSlots);
        }
        if(i == -1) return;
        classNow = enrolledSlots[i].split("-");
        course = classNow[1];
        venue = classNow[2];
        if(classNow.length > 3) venue += "-" + classNow[3];
        if(classNow.length > 4) venue += "-" + classNow[4];
        /*
            Notification
         */
        String channelID = "AttendanceUpdate";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), channelID);
        Intent ii = new Intent(context.getApplicationContext(),Attendance.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 10, ii, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Click here to update your attendance details. Ignore if class was cancelled."); //detail mode is the "expanded" notification
        bigText.setBigContentTitle("Attendance for "+course);

        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_k_notification);// this is the white image with transparent background
        mBuilder.setColor(0xFF98652A);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_k_notification));//notification icon
        mBuilder.setContentTitle("Attendance for "+course); //main title
        mBuilder.setContentText("Did you attend your "+course+" class at "+venue+"?"); //main text when you "haven't expanded" the notification yet
        mBuilder.setPriority(NotificationManager.IMPORTANCE_HIGH);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(channelID,
                "Attendance Update", NotificationManager.IMPORTANCE_HIGH);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(channel);
        }

        if (mNotificationManager != null) {
            mNotificationManager.notify(generateUniqueId(course), mBuilder.build());
        }
        /*
            Notification
        */


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

    int generateUniqueId(String slot) {
        // Generate a unique ID based on current timestamp and slot information
        String uniqueId = "ClassNotification_" + slot + "_" + System.currentTimeMillis();
        return uniqueId.hashCode();
    }

}
