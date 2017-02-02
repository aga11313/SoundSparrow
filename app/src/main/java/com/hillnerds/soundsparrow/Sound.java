package com.hillnerds.soundsparrow;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;


public class Sound extends AppCompatActivity implements MidiDriver.OnMidiStartListener {
    protected MidiDriver midi;
    protected MediaPlayer player;

    protected long userId = MainActivity.uuid_long;
    protected long randomSeed = userId;

    public Random randomGenerator;
    int channelCounter = 0;


    public ArrayList<Channel> channelList = new ArrayList<>();

    private Thread playThread;
    private boolean pausePressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

        // TODO: dynamically get emotion
        channelList.add(new Channel(userId, "happy", 0, 1));

        //generate random number
        initializeRandomNumberGenerator();

        midi = new MidiDriver();

        if (midi != null) {
            midi.setOnMidiStartListener(this);
        } else {
            Log.w("Sound", "midi object was null");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (midi != null) {
            midi.start();
        }

        ArrayList<Note> noteArray = generate_midi_file(channelList);

        ArrayList<Note> sortedNoteArray = sort_midi_array(noteArray);

        MidiParser mp = new MidiParser(sortedNoteArray);
        playThread = new Thread(mp);
        playThread.start();

        BluetoothHelper bHelp = new BluetoothHelper(this, new BluetoothHelper.SparrowDiscoveredCallback() {
            @Override
            public void onSparrowDiscovered(UUID uuid, String emotion, int rssi) {
                Log.i("Sound", "A sparrow has been discovered");

                long hi = uuid.getMostSignificantBits() & Long.MAX_VALUE;

                Channel channel = new Channel(hi, emotion, channelCounter, 1);
                channelList.add(channel);
                generateColor(emotion);

                channelCounter++;
            }
        }, BluetoothHelper.getDeviceUUID(this));
        bHelp.bleStateMachine();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (midi != null) {
            midi.stop();
        }

        // Stop player
        if (player != null) {
            player.stop();
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
        // TODO: Decide if this needs to do anything
    }

    public void initializeRandomNumberGenerator() {
        long random_seed_num = randomSeed;
        randomGenerator = new Random(random_seed_num);
    }

    public void safeSleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Note> generateMidiChannel(Channel c) {
        ArrayList<Note> noteArray = new ArrayList<>();

        int timestampCounter = 0;

        c.generateInstrument(randomGenerator);

        Note instrument_change = new Note(192 + c.number, c.instrument, timestampCounter);
        noteArray.add(instrument_change);

        for (int i = 0; i < 16; i = i+2) {
            int startingCode = SoundGeneration.generate_starting_code(c.number);
            int pitch = SoundGeneration.generate_pitch(randomGenerator, c.range_min, c.range_max);
            int velocity = SoundGeneration.generate_velocity(randomGenerator);

            int duration = SoundGeneration.generate_note_duration(randomGenerator);

            Note note_on = new Note(startingCode, pitch, velocity, timestampCounter);
            timestampCounter = timestampCounter + duration;

            Note note_off = new Note(startingCode, pitch, 0, timestampCounter);

            noteArray.add(note_on);
            noteArray.add(note_off);
        }
        return noteArray;
    }

    public ArrayList <Note> generate_midi_file(ArrayList<Channel> channelList){
        ArrayList<ArrayList<Note>> midiToMerge = new ArrayList<>();

        for (Channel c : channelList) {
            midiToMerge.add(generateMidiChannel(c));
        }

        ArrayList<Note> combined = new ArrayList<>();

        for (ArrayList<Note> a_m : midiToMerge){
            combined.addAll(a_m);
        }

        return combined;
    }

    public ArrayList<Note> sort_midi_array (ArrayList<Note> noteArray){
        Collections.sort(noteArray,new Comparator<Note>() {
            @Override
            public int compare(Note m1, Note m2) {
                return Integer.valueOf(m1.timestamp).compareTo(m2.timestamp);
            }
        });

        return noteArray;
    }

    public class MidiParser implements Runnable {
        private ArrayList<Note> midiFile;

        public MidiParser(ArrayList<Note> midiFile) {
            this.midiFile = midiFile;
        }

        public void run() {
            while (!pausePressed)
            {
                int currentTimestamp = 0;

                for (Note m : midiFile) {
                    if (pausePressed) {
                        break;
                    }
                    if (m.isStartingBit) {
                        sendMidi(m.starting_code, m.instrument);
                    } else {
                        safeSleep(m.timestamp - currentTimestamp);
                        sendMidi(m.starting_code, m.pitch, m.velocity);
                    }
                    currentTimestamp = m.timestamp;
                }
            }
        }
    }

    public void generateColor(String emotion){
        // TODO: Find a better view than TextView for a block of colour
        TextView text1 = (TextView)findViewById(R.id.text1);
        TextView text2 = (TextView)findViewById(R.id.text2);
        TextView text3 = (TextView)findViewById(R.id.text3);
        TextView text4 = (TextView)findViewById(R.id.text4);

        ArrayList<TextView> textArray = new ArrayList<>();
        textArray.add(text1);
        textArray.add(text2);
        textArray.add(text3);
        textArray.add(text4);

        int[] warmColorArray = new int[] {Color.RED, Color.YELLOW, Color.MAGENTA};
        int[] coldColorArray = new int[] {Color.BLUE, Color.CYAN, Color.GRAY, Color.GREEN};

        if (emotion == "happy"){
            textArray.get(channelCounter).setBackgroundColor(
                    warmColorArray[randomGenerator.nextInt((2-0) +1) +0]);
        } else if (emotion == "sad"){
            textArray.get(channelCounter).setBackgroundColor(
                    coldColorArray[randomGenerator.nextInt((2-0) +1) +0]);
        } else {
            Log.w("Sound", "generateColor received a bad emotion");
        }
    }
}

