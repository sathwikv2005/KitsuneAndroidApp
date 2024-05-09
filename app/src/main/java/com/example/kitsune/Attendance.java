package com.example.kitsune;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;

public class Attendance extends AppCompatActivity {

    ListView lvAttendance;
    EditText miniP;
    int miniPercent;
    AttendanceDB db = new AttendanceDB(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendance);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        miniP = findViewById(R.id.miniPercent);
        // Check if miniP is not null and its text is not empty before parsing
        if (miniP != null && miniP.getText() != null && !miniP.getText().toString().isEmpty()) {
            miniPercent = Integer.parseInt(miniP.getText().toString().split("%")[0]);
        } else {
            // Handle the case where miniP is null or its text is empty
            miniPercent = 80; // Or set a default value
        }
        updatePage();
    }

    public void endAttendance(View v){
        finish();
    }

    public void updateAttendance(String course, int p, int tc){
        db.update(course,p,tc);
        updatePage();
    }
    public void present(String course, boolean p){
        db.present(course,p);
        updatePage();
    }
    public int needToAttend(int p, int tc){
        double per = ((float) p /tc) * 100 ;
        int percent = (int) Math.ceil(per);
        int i = 0;
        while(percent < miniPercent){
            i++;
            percent = (int) Math.ceil(((float)(p+i)/(tc+i)) * 100);
        }
        return i;
    }
    public void updatePage(){
        lvAttendance = findViewById(R.id.lvAttendance);
        String[] slots = db.getData();
        AttendanceArrayAdapter slotAdapter = new AttendanceArrayAdapter(this, R.layout.list_item, Arrays.asList(slots));
        lvAttendance.setAdapter(slotAdapter);

    }

    public void miniAttendanceUpdate(View v){
        miniPercent = Integer.parseInt(miniP.getText().toString().split("%")[0]);
        updatePage();
    }

}