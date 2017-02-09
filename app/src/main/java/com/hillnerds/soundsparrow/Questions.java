package com.hillnerds.soundsparrow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.text.MessageFormat;

public class Questions extends AppCompatActivity {
    protected EditText nameEdit;
    protected EditText linkedinEdit;
    protected EditText githubEdit;
    private SharedPreferences appPref;
    private final String PREF_NAME = "SparrowPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        nameEdit        = (EditText) findViewById(R.id.name_edit_text);
        linkedinEdit    = (EditText) findViewById(R.id.linkedin_edit_text);
        githubEdit      = (EditText) findViewById(R.id.github_edit_text);

        // fill edittexts with existing preferences
        appPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        nameEdit.setText(appPref.getString("user_name", ""));
        linkedinEdit.setText(appPref.getString("linkedin_id", ""));
        githubEdit.setText(appPref.getString("github_id", ""));

       View.OnKeyListener closeOnEnter = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nameEdit.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        };

        nameEdit.setOnKeyListener(closeOnEnter);
        linkedinEdit.setOnKeyListener(closeOnEnter);
        githubEdit.setOnKeyListener(closeOnEnter);
    }

    public void validateAndFinish(View view){
        if (nameEdit.getText().toString().trim().length() == 0) {
            Toast.makeText(this, R.string.no_name_toast, Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences.Editor ed = appPref.edit();
        ed.putString("user_name", nameEdit.getText().toString());
        ed.putString("linkedin_id", linkedinEdit.getText().toString());
        ed.putString("github_id", githubEdit.getText().toString());
        ed.commit();

        finish();
    }
}
