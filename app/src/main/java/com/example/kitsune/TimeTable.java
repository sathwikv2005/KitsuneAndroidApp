package com.example.kitsune;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TimeTable extends AppCompatActivity {

    DBManager db = new DBManager(this);
    AttendanceDB aDB = new AttendanceDB(this);
    ListView lvSlots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_time_table);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        updatePage();

    }
    public void end(View v){
        finish();
    }
    public void addSlot(View v){
        EditText courseET = findViewById(R.id.course);
        EditText venueET = findViewById(R.id.venue);
        EditText slotET = findViewById(R.id.slot);
        EditText totalClassesET = findViewById(R.id.total_classes);
        EditText presentET = findViewById(R.id.present);
        String course = courseET.getText().toString();
        String venue = venueET.getText().toString();
        String sTotalClasses = totalClassesET.getText().toString();
        String sPresent = presentET.getText().toString();
        int totalClasses;
        int present;
        // Check if EditText fields are empty
        if (sTotalClasses.isEmpty()) {
            totalClasses = 0;
        } else {
            totalClasses = Integer.parseInt(sTotalClasses);
        }
        if (sPresent.isEmpty()) {
            present = 0;
        }else {
            present = Integer.parseInt(sPresent);
        }

        String[] slots = slotET.getText().toString().split("\\+");
        if(course.isEmpty() || venue.isEmpty() || slotET.getText().toString().isEmpty()) {
            Toast.makeText(this,"Invalid parameters provided",Toast.LENGTH_LONG).show();
            return;
        }
        for(String slot : slots){
            boolean addToDB = db.addSlot(slot,course,venue);
            if(addToDB) Toast.makeText(TimeTable.this,"Successfully Added",Toast.LENGTH_SHORT).show();
            else Toast.makeText(TimeTable.this,"An error occurred, Try again later",Toast.LENGTH_SHORT).show();
        }
        boolean addToDB = aDB.addCourse(course,totalClasses,present);
        if(!addToDB) Toast.makeText(TimeTable.this,"An error occurred when trying to add attendance, Try again later",Toast.LENGTH_SHORT).show();
        updatePage();


    }
    public void delSlot(View v){
        EditText delSlotT = findViewById(R.id.delSlot);
        String[] delSlots = delSlotT.getText().toString().split("\\+");
        if(delSlotT.getText().toString().isEmpty()) {
            Toast.makeText(this,"Invalid parameters provided",Toast.LENGTH_LONG).show();
            return;
        }
        for (String slot: delSlots){
            boolean del = db.deleteSlot(slot);
            if(del) Toast.makeText(this,"Successfully Deleted!", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this,"Failed to Delete slot: "+slot, Toast.LENGTH_LONG).show();
        }
        updatePage();
    }
    protected void updatePage(){
        lvSlots = findViewById(R.id.lvSlots);
        String[] slots = db.getData();
        ArrayAdapter<String> slotAdapter = new ArrayAdapter<>(this, R.layout.list_item,slots);
        lvSlots.setAdapter(slotAdapter);

    }
}