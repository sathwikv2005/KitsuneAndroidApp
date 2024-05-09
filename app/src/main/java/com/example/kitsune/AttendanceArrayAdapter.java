package com.example.kitsune;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AttendanceArrayAdapter extends ArrayAdapter<String> {
    public AttendanceArrayAdapter(Context context, int resource,List<String> objects) {
        super(context, 0, objects);
    }
    Attendance activity = null;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String[] str = getItem(position).split("-");
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.attendance_card, parent, false);
        }
        if (getContext() instanceof Attendance) {
            activity = (Attendance) getContext();
        }
        int tc = Integer.parseInt(str[1]);
        int p = Integer.parseInt(str[2]);
        ImageButton refresh = convertView.findViewById(R.id.refreshAttendanceBtn);
        Button presentButton = convertView.findViewById(R.id.button);
        Button absentButton = convertView.findViewById(R.id.button3);
        Button noClassButton = convertView.findViewById(R.id.button4);
        TextView courseTitle = convertView.findViewById(R.id.courseTitleAttendance);
        TextView present = convertView.findViewById(R.id.presentAttendance);
        TextView totalClasses = convertView.findViewById(R.id.totalClassesAttendance);
        TextView percentage = convertView.findViewById(R.id.percentAttendance);
        TextView needToAttend = convertView.findViewById(R.id.needToAttend);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseTitleText = str[0];
                int a = Integer.parseInt( present.getText().toString());
                int b = Integer.parseInt(totalClasses.getText().toString());

                if (activity != null) activity.updateAttendance(courseTitleText, a, b);
            }
        });
        presentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseTitleText = str[0];
                if (activity != null) activity.updateAttendance(courseTitleText,p+1, tc+1);
            }
        });
        absentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String courseTitleText = str[0];
                if (activity != null) activity.updateAttendance(courseTitleText,p, tc+1);
            }
        });
        noClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity != null) activity.updatePage();
            }
        });
        courseTitle.setText(str[0]);
        totalClasses.setText(str[1]);
        present.setText(str[2]);
        double per = ((float) p /tc) * 100 ;
        int percent = (int) Math.ceil(per);
        String pString = "Attendance: "+percent+"%";
        percentage.setText(pString);
        int i = 0;
        if (activity != null){
            i = activity.needToAttend(p,tc);
        }
        String nTaString = "Need to attend: "+i;
        needToAttend.setText(nTaString);
        return convertView;
    }


}


