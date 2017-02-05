package com.hillnerds.soundsparrow;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.MessageFormat;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private String[] requested = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_PHONE_STATE
    };

    public static UUID uuid;
    public String uuid_str;
    public static long uuid_long;

    private final String PREF_NAME = "SparrowPreferences";
    private SharedPreferences appPref;
    private TextView greetings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        greetings = (TextView) findViewById(R.id.greeting);

        askPermissions();

        if(appPref.getString("user_name", "") == "") {
            askQuestions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        greetings.setText("Hi, " + appPref.getString("user_name", ""));
    }

    public void openSound(View view) {
        Intent intent = new Intent(this, Sound.class);
        startActivity(intent);
    }

    public void editDataClick(View view) {
        askQuestions();
    }

    public void askQuestions() {
        Intent intent = new Intent(this, Questions.class);

        startActivity(intent);
    }

    private void askPermissions() {
        int idx = 0;
        for (String permission : requested) {
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    permission);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.i("Permissions", MessageFormat.format("Requesting permission {0}", permission));
                ActivityCompat.requestPermissions(this,
                        new String[] {permission},
                        idx);
            }
            idx++;
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length == 0
                || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,
                    MessageFormat.format("Permission {0} not granted", requested[requestCode]),
                    Toast.LENGTH_LONG);
        }
    }
}
