package com.hillnerds.soundsparrow;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.Locale;


public class Sound extends AppCompatActivity implements MidiDriver.OnMidiStartListener, View.OnClickListener, View.OnTouchListener {

    protected MidiDriver midi;
    protected MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        //ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
        //layout.addView(textView);

        View play_notes_button = findViewById(R.id.play_notes);
        if (play_notes_button != null)
            play_notes_button.setOnClickListener(this);

        midi = new MidiDriver();

        if (midi != null)
            midi.setOnMidiStartListener(this);

        //player = MediaPlayer.create(this, R.raw.play);
        //player.start();

    }

    @Override
    public void onMidiStart()
    {
        // Program change - harpsicord - change instruments over here

        sendMidi(0xc0, 6);

        // Get the config

        //int config[] = midi.config();

        //Resources resources = getResources();

        //String format = resources.getString(R.string.format);
        //String info = String.format(Locale.getDefault(), format, config[0],
        //        config[1], config[2], config[3]);

    }

    @Override
    public void onResume(){
        super.onResume();

        if (midi != null)
            midi.start();
        //start();

    }

    @Override
    public void onPause(){

        super.onPause();

        if (midi != null)
            midi.stop();

        // Stop player

        if (player != null)
            player.stop();

    }

    protected void sendMidi(int m, int p)
    {
        byte msg[] = new byte[2];

        msg[0] = (byte) m;
        msg[1] = (byte) p;

        midi.write(msg);
    }

    protected void sendMidi(int m, int n, int v)
    {
        byte msg[] = new byte[3];

        msg[0] = (byte) m;
        msg[1] = (byte) n;
        msg[2] = (byte) v;

        midi.write(msg);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {


        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.play_notes:
                sendMidi(0xc0, 57);
                sendMidi(0x90, 48, 63);
                sendMidi(0x90, 52, 63);
                sendMidi(0x90, 55, 63);
                break;
            case R.id.stop_notes:
                sendMidi(0x80, 48, 0);
                sendMidi(0x80, 52, 0);
                sendMidi(0x80, 55, 0);
                break;
        }

    }
}
