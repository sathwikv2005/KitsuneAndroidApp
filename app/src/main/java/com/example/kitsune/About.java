package com.example.kitsune;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class About extends AppCompatActivity {
    String version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView versionDisplay = findViewById(R.id.versionDisplay);
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        version = info.versionName;
        String t = "Version: v"+version;
        versionDisplay.setText(t);
    }
    public void checkForUpdates(View v){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://api.github.com/repos/sathwikv2005/KitsuneAndroidApp/releases/latest", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String latest = null;
                try {
                    latest = jsonObject.getString("tag_name");
                } catch (JSONException e) {
                    Toast.makeText(About.this,"Error occurred while checking for new versions.",Toast.LENGTH_SHORT).show();
                }
                if(!latest.equalsIgnoreCase("v"+version)){
                    dialog(latest);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(About.this,"Error occurred while checking for new versions.",Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
    void dialog(String latest){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.update_app_dialog);
        TextView dialogClose = dialog.findViewById(R.id.dialogCloseAbout);
        TextView updateNow = dialog.findViewById(R.id.updateNowAbout);
        dialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        updateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/sathwikv2005/KitsuneAndroidApp/releases");
                Intent intent= new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public void goToGitHub(View v){
        Uri uri = Uri.parse("https://github.com/sathwikv2005/KitsuneAndroidApp/releases");
        Intent intent= new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }
}