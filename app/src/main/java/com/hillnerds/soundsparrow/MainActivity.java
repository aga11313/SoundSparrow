package com.hillnerds.soundsparrow;

import android.app.Activity;
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
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askPermissions();

        TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tManager.getDeviceId();

        // 32 characters so that correct number of hex octets
        String uuidString = deviceId + deviceId + "00";
        String correctFormatUuid = uuidString.substring(0, 8) + '-' +
                uuidString.substring(8, 12) + '-' +
                uuidString.substring(12, 16) + '-' +
                uuidString.substring(16, 20) + '-' +
                uuidString.substring(20, 32);


        UUID uuid = UUID.fromString(correctFormatUuid);


    }

    public void openSound() {
        Intent intent = new Intent(this, Sound.class);
        startActivity(intent);
    }

    public void askQuestions(View view){
        Intent intent = new Intent(this, Questions.class);
        startActivityForResult(intent, 1);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                TextView greeting = (TextView)findViewById(R.id.greeting);
                greeting.setText("Welcome " + result + " :)");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        Button button = (Button)findViewById(R.id.get_started);

        ((ViewManager)button.getParent()).removeView(button);



        ImageButton play = (ImageButton)findViewById(R.id.click_me);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSound();
            }
        });
        
    }
}
