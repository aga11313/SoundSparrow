package com.hillnerds.soundsparrow;

import android.graphics.Color;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by aga on 28/01/17.
 */

/**
 * A class handling the merging and sorting of MIDI Sequences generated for all channels by the
 * Channel Generation class.
 */
public class SoundGeneration {

    int[][] midi_file;

    int number_of_channels = 4;

    /**
     * Method that is called from the Sound class onResume method. Responsible for generating a
     * full MIDI Array of MidiSequences ready for synthesizing.
     * @param activeChannelList - the List of currently active (in range) channels
     * @return - the sorrted and ready for sythesis array of MidiSequence objects.
     */
    public static ArrayList<MidiSequence> createFileToSynthesize (ArrayList<Channel> activeChannelList){

        ArrayList<MidiSequence> midiSequenceArray = generateMidiFile(activeChannelList);
        ArrayList<MidiSequence> sortedMidiSequenceArray = sortMidiSequenceArray(midiSequenceArray);

        return sortedMidiSequenceArray;
    }

    /**
     * Generates MidiSequence Arrays for all channels in the Channel sound generation class.
     * Stores all of them in one ArrayList
     * @param activeChannelList - list of sparrows currently in range
     * @return - an ArrayList of MidiSeqence ArrayLists for all available channel
     */
    public static ArrayList <MidiSequence> generateMidiFile(ArrayList<Channel> activeChannelList){
        ArrayList<ArrayList<MidiSequence>> midiToMerge = new ArrayList<>();

        for (Channel c : activeChannelList) {
            midiToMerge.add(ChannelSoundGeneration.generateMidiChannel(c));
        }

        ArrayList<MidiSequence> combined = new ArrayList<>();

        for (ArrayList<MidiSequence> ms : midiToMerge){
            combined.addAll(ms);
        }

        return combined;
    }

    /**
     * Sorts an Arraylist of MidiSequence Objects by their timestamp
     * @param midiSequenceArray - the array to be sorted
     * @return a sorted array
     */
    public static ArrayList<MidiSequence> sortMidiSequenceArray(ArrayList<MidiSequence> midiSequenceArray){
        Collections.sort(midiSequenceArray,new Comparator<MidiSequence>() {

            @Override
            public int compare(MidiSequence m1, MidiSequence m2) {
                return Integer.valueOf(m1.timestamp).compareTo(m2.timestamp);
            }
        });

        return midiSequenceArray;
    }

    // TODO: implement the visualisation element again
    /*public void generateColor(String emotion){
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
    }*/
}
