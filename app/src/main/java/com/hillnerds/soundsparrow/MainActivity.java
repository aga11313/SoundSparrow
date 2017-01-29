package com.hillnerds.soundsparrow;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;



public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.hillnerds.soundsparow.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //String uuid = tManager.getDeviceId();

        //Log.i("uuid", String.format("UUid: %1$d :", uuid));

        Sound sound = new Sound();

    }

    public void openSound(View view) {
        Intent intent = new Intent(this, Sound.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = "Hello";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
