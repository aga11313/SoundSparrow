package com.hillnerds.soundsparrow;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.billthefarmer.mididriver.MidiDriver;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;


public class Sound extends AppCompatActivity implements MidiDriver.OnMidiStartListener {
    protected MidiDriver midi;

    protected long userId = MainActivity.uuid_long;
    //protected long randomSeed = userId;

    //public Random randomGenerator;
    int channelCounter = 0;

    private Thread playThread;

    public ArrayList<Channel> activeChannelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

        //create a channel number reserved for the user of the app
        // TODO: dynamically get emotion (hardcoded for now)

        activeChannelList.add(new Channel(userId, "happy", 0));

        midi = new MidiDriver();

        if (midi != null) {
            midi.setOnMidiStartListener(this);
        } else {
            Log.w("Sound", "midi object was null");
        }
    }

    /**
     * The Sound Generation bit takes place in this stage of the Activity cycle.
     * The activeChannelList is processed to create a complete file of MIDI sequences and
     * pass it on to the synthesizer.
     */
    @Override
    public void onResume() {
        super.onResume();

        if (midi != null) {
            midi.start();
        }

        ArrayList<MidiSequence> sortedMidiSequenceArray = SoundGeneration.createFileToSynthesize(activeChannelList);

        Log.i("onResume", "MidiParser reached");

        MidiParser mp = new MidiParser(sortedMidiSequenceArray);
        playThread = new Thread(mp);
        playThread.start();

        BluetoothHelper bHelp = new BluetoothHelper(this, new BluetoothHelper.SparrowDiscoveredCallback() {
            @Override
            public void onSparrowDiscovered(UUID uuid, String emotion, int rssi) {
                Log.i("Sound", "A sparrow has been discovered");

                long hi = uuid.getMostSignificantBits() & Long.MAX_VALUE;

                /* Every time a new Sparrow is discovered a new Channel is added
                 * to the list of active Channels.
                 */
                Channel discoveredChannel = new Channel(hi, emotion, channelCounter);
                activeChannelList.add(discoveredChannel);
                //TODO: implement the visualisation element again
                //generateColor(emotion);

                channelCounter++;
            }
        }, BluetoothHelper.getDeviceUUID(this));
        bHelp.bleStateMachine();
    }

    /**
     *
     */
    @Override
    public void onPause() {
        super.onPause();

        if (midi != null) {
            midi.stop();
        }
    }

    /**
     * Sends a two byte array to the MIDI stream. Two byte sequences are Starting Sequences
     * that issue instrument changes.
     * @param m - channel code
     * @param p - starting sequence code
     */
    protected void sendMidi(int m, int p) {
        byte msg[] = new byte[2];

        msg[0] = (byte) m;
        msg[1] = (byte) p;

        //Writes the two byte array to the MIDI stream.
        midi.write(msg);
    }

    /**
     * Sends a three byte array to the MIDI stream. Three byte sequences are Notes.
     * @param m - the code sequence. Indicates the purpose of this particular MIDI sequence
     * @param n - the pitch of the note
     * @param v - the velocity of the note
     */
    protected void sendMidi(int m, int n, int v) {
        byte msg[] = new byte[3];

        msg[0] = (byte) m;
        msg[1] = (byte) n;
        msg[2] = (byte) v;

        //Writes the three byte array to the MIDI stream
        midi.write(msg);
    }

    @Override
    public void onMidiStart() {
        // TODO: Decide if this needs to do anything
    }

    /**
     * A safe sleep function including a try catch statement to capture exceptions.
     * Created to avoid using try catch every time a Thread sleeps.
     * @param time - a sleep time
     */
    public void safeSleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public class MidiParser implements Runnable {
        private ArrayList<MidiSequence> midiSequenceFile;

        public MidiParser(ArrayList<MidiSequence> midiSequenceFile) {
            this.midiSequenceFile = midiSequenceFile;
        }

        public void run() {
            int currentTimestamp = 0;


            for (MidiSequence m : midiSequenceFile) {

                if (m instanceof StartingSequence) {
                    Log.i("MidiParser", MessageFormat.format("The SS of MidiSequenceFile {0}, {1}",m.startingCode, m.timestamp));
                    sendMidi(m.startingCode, ((StartingSequence) m).instrument.instrumentMidiCode);
                } else if (m instanceof  Note){
                    Log.i("MidiParser", MessageFormat.format("The N of MidiSequenceFile {0}, {1}, {2}",
                            ((Note) m).pitch, ((Note) m).velocity, m.timestamp));
                    safeSleep(m.timestamp - currentTimestamp);
                    sendMidi(m.startingCode, ((Note) m).pitch, ((Note) m).velocity);
                }
                currentTimestamp = m.timestamp;
            }
        }
    }

}

