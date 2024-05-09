package com.example.kitsune;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.work.PeriodicWorkRequest;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class StartUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            LocalDateTime now = LocalDateTime.now();
            int min = now.getMinute();
            if(min%15 != 0){
                int reminder = min%15;
                min = 15-reminder;
            }
            PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(UpcomingClassCheck.class,15, TimeUnit.MINUTES).setInitialDelay(min,TimeUnit.MINUTES).build();
            /*
             * Notification
             */
            String channelID = "ClassReminders";
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelID);
            Intent ii = new Intent(context,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 10, ii, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText("Initial setup complete"); //detail mode is the "expanded" notification
            bigText.setBigContentTitle("Initial setup complete");

            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher); //notification icon
            mBuilder.setContentTitle("Initial setup complete"); //main title
            mBuilder.setContentText("Initial setup complete"); //main text when you "haven't expanded" the notification yet
            mBuilder.setPriority(NotificationManager.IMPORTANCE_LOW);
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
        }

    }
}
