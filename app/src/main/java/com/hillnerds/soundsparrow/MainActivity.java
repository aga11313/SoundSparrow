package com.hillnerds.soundsparrow;

import android.content.Context;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.MessageFormat;



public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.hillnerds.soundsparow.MESSAGE";

    private String[] requested = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //String uuid = tManager.getDeviceId();

        //Log.i("uuid", String.format("UUid: %1$d :", uuid));
        askPermissions();

        Sound sound = new Sound();

    }

    public void openSound(View view) {
        Intent intent = new Intent(this, Sound.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = "Hello";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private void askPermissions() {
        int idx = 0;
        for (String permission : requested) {
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    permission);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
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
