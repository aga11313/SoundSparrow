package com.hillnerds.soundsparrow;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;


public class Sound extends AppCompatActivity implements MidiDriver.OnMidiStartListener, View.OnClickListener, View.OnTouchListener {
    protected MidiDriver midi;
    protected MediaPlayer player;

    protected int user_id = 112233;
    protected int random_seed = user_id;

    public Random random_generator;

    public ArrayList<Channel> channel_list;

    private Thread playThread;
    private boolean pausePressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

        channel_list = new ArrayList<>();
        Channel channel1 = new Channel(112233, 1, 0, 1);
        channel_list.add(channel1);

        //generate random number
        initialize_random_number_generator();

        midi = new MidiDriver();

        if (midi != null) {
            midi.setOnMidiStartListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (midi != null)
            midi.start();

        ArrayList<Midi> midi_array;
        midi_array = generate_midi_file(channel_list);

        ArrayList<Midi> sorted_midi_array = sort_midi_array(midi_array);

        MidiParser mp = new MidiParser(sorted_midi_array);
        playThread = new Thread(mp);
        playThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (midi != null)
            midi.stop();

        // Stop player

        if (player != null)
            player.stop();
    }

    public void pauseClick(View v) {
        pausePressed = true;
        try {
            playThread.join(100);
        } catch (InterruptedException e) {
            Log.w("Sound", "Couldn't stop playThread");
        }
    }

    protected void sendMidi(int m, int p) {
        byte msg[] = new byte[2];

        msg[0] = (byte) m;
        msg[1] = (byte) p;

        midi.write(msg);
    }

    protected void sendMidi(int m, int n, int v) {
        byte msg[] = new byte[3];

        msg[0] = (byte) m;
        msg[1] = (byte) n;
        msg[2] = (byte) v;

        midi.write(msg);
    }

    @Override
    public void onMidiStart() {

    }

    public void initialize_random_number_generator() {
        int random_seed_num = random_seed;
        random_generator = new Random(random_seed_num);
    }

    public void safeSleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Midi> generate_midi_channel(Channel c) {
        ArrayList<Midi> midi_array = new ArrayList<>();

        int timestamp_counter = 0;

        c.generateInstrument(random_generator);

        Midi instrument_change = new Midi(192 + c.number, c.instrument, timestamp_counter);
        midi_array.add(instrument_change);

        for (int i = 0; i < 16; i = i+2) {
            int starting_code = SoundGeneration.generate_starting_code(c.number);
            int pitch = SoundGeneration.generate_pitch(random_generator, c.range_min, c.range_max);
            int velocity = SoundGeneration.generate_velocity(random_generator);

            int duration = SoundGeneration.generate_note_duration(random_generator);

            Midi note_on = new Midi(starting_code, pitch, velocity, timestamp_counter);
            timestamp_counter = timestamp_counter + duration;

            Midi note_off = new Midi(starting_code, pitch, 0, timestamp_counter);

            midi_array.add(note_on);
            midi_array.add(note_off);
        }
        return midi_array;
    }

    public ArrayList <Midi> generate_midi_file(ArrayList<Channel> channel_list){
        ArrayList<ArrayList<Midi>> midi_to_merge = new ArrayList<ArrayList<Midi>>();

        for (Channel c : channel_list) {
            midi_to_merge.add(generate_midi_channel(c));
        }

        ArrayList<Midi> combined = new ArrayList<Midi>();

        for (ArrayList<Midi> a_m : midi_to_merge){
            combined.addAll(a_m);
        }

        return combined;
    }

    public ArrayList<Midi> sort_midi_array (ArrayList<Midi> midi_array){
        Collections.sort(midi_array,new Comparator<Midi>() {
            @Override
            public int compare(Midi m1, Midi m2) {
                return Integer.valueOf(m1.timestamp).compareTo(m2.timestamp);
            }
        });

        return midi_array;
    }

    public class MidiParser implements Runnable {
        private ArrayList<Midi> midiFile;

        public MidiParser(ArrayList<Midi> midiFile) {
            this.midiFile = midiFile;
        }

        public void run() {
            while (!pausePressed)
            {
                int current_timestamp = 0;

                for (Midi m : midiFile) {
                    if (pausePressed) {
                        break;
                    }
                    if (m.isStartingBit) {
                        sendMidi(m.starting_code, m.instrument);
                    } else {
                        safeSleep(m.timestamp - current_timestamp);
                        sendMidi(m.starting_code, m.pitch, m.velocity);
                    }
                    current_timestamp = m.timestamp;
                }
            }
        }
    }
}

