package com.hillnerds.soundsparrow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //JavaSample javaSample = new JavaSample();

        //javaSample.emotionRecognition();

        EmotionService emotionService = new EmotionService();
        emotionService.execute();
    }
}
