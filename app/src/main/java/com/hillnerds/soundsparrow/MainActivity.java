package com.hillnerds.soundsparrow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.hillnerds.soundsparow.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
