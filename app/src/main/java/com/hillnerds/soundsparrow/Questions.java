package com.hillnerds.soundsparrow;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Questions extends AppCompatActivity {

    protected EditText edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        edit_text = (EditText)findViewById(R.id.edit_text);

    }

    public void send_results(View view){

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", edit_text.getText().toString());
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }
}
