package com.example.kitsune;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.navigation.NavigationView;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    //TODO: Add a reminder feature.

    DrawerLayout drawerLayout;
    ImageButton burger;
    NavigationView navigationView;
    SharedPreferences prefs = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Check and ask for notification permission if not enabled.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
            }
        }


        createBurgerMenu(); //Burger Menu

        updateUpcomingClassesCard(); //Update upcoming class display card


    }
    protected void onResume() {
        super.onResume();

        updateUpcomingClassesCard(); //UpcomingClassDisplay

        firstRun(); //First Run
    }

    void updateUpcomingClassesCard(){
        TextView upcomingClassDisplay = findViewById(R.id.UpcomingClassDisplay);
        CardView upcomingClassCard = findViewById(R.id.upComingClassCard);
        SlotsManager slotsManager = new SlotsManager();
        String upcomingClasses = slotsManager.getUpcomingClass(this);
        if(!upcomingClasses.equalsIgnoreCase("-1")){
            upcomingClassCard.setVisibility(View.VISIBLE);
            upcomingClassDisplay.setText(upcomingClasses);
        }
        else {
            upcomingClassCard.setVisibility(View.GONE);
        }
    }

    void firstRun(){
        if (prefs.getBoolean("firstRun", true)) {
            setUpPeriodicWorkRequest();
        }
    }

    void setUpPeriodicWorkRequest(){
        LocalDateTime now = LocalDateTime.now();
        int min = now.getMinute();
        if(min%15 != 0){
            int reminder = min%15;
            min = 15-reminder;
        }
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(UpcomingClassCheck.class,15, TimeUnit.MINUTES).setInitialDelay(min,TimeUnit.MINUTES).build();

        WorkManager.getInstance(MainActivity.this).enqueue(periodicWorkRequest);
        prefs.edit().putBoolean("firstRun", false).commit();
        Log.d("KitsuneLog","firstRun occurred");
        /*
         * Notification
         */
        String channelID = "ClassReminders";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.getApplicationContext(), channelID);
        Intent ii = new Intent(this.getApplicationContext(),MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 10, ii, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Initial setup complete"); //detail mode is the "expanded" notification
        bigText.setBigContentTitle("Initial setup complete");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher); //notification icon
        mBuilder.setContentTitle("Initial setup complete"); //main title
        mBuilder.setContentText("Initial setup complete"); //main text when you "haven't expanded" the notification yet
        mBuilder.setPriority(NotificationManager.IMPORTANCE_LOW);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

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

    void createBurgerMenu(){
        prefs = getSharedPreferences("Kitsune", MODE_PRIVATE);

        drawerLayout = findViewById(R.id.main);
        burger = findViewById(R.id.burger);

        burger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.timetableMenu){
                    Intent timetable = new Intent(MainActivity.this, TimeTable.class);
                    startActivity(timetable);
                }
                if(id == R.id.attendanceMenu){
                    Intent attendance = new Intent(MainActivity.this, Attendance.class);
                    startActivity(attendance);
                }
                if(id == R.id.aboutMenu){
                    Intent about = new Intent(MainActivity.this, About.class);
                    startActivity(about);
                }
                drawerLayout.close();
                return false;
            }
        });
    }
}