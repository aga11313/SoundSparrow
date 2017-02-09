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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_PHONE_STATE
    };

    private final String PREF_NAME = "SparrowPreferences";
    private SharedPreferences appPref;
    private TextView greetings;

    final static int PERMISSIONS_REQUEST_CODE = 1;

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
        int permissionCheck;
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            permissionCheck = ContextCompat.checkSelfPermission(this, p);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(
                    new String[listPermissionsNeeded.size()]), PERMISSIONS_REQUEST_CODE);

        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length > 0){
            switch (requestCode) {
                case PERMISSIONS_REQUEST_CODE:
                    for (int i = 0; i < grantResults.length; i++) {
                        switch (grantResults[i]) {
                            case (PackageManager.PERMISSION_GRANTED):
                                Log.i("onRequestPermission", "Permission Granted");
                            case (PackageManager.PERMISSION_DENIED):
                                Log.i("onRequestPermission", "Permission Denied");
                                Toast.makeText(this,
                                        MessageFormat.format("Permission {0} not granted",
                                                permissions[i]), Toast.LENGTH_LONG).show();
                        }

                    }
            }
            return;
        }
    }
}
