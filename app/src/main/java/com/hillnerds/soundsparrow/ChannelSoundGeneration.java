package com.hillnerds.soundsparrow;

import android.util.Log;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by aga on 06/02/17.
 */

public class ChannelSoundGeneration {

    int scale;
    int[] steps;

    //a hard coded list of all possible instruments
    private static Instrument[] instrumentList = new Instrument[] {
            new Instrument("piano", 1, 40, 60),
            new Instrument("guitar", 26, 45, 70),
            new Instrument("trombone", 58, 55, 70),
            new Instrument("trumpet", 57, 64, 80),
            new Instrument("violin", 41, 50, 65),
            new Instrument("saxophone", 66, 45, 60),
            new Instrument("flute", 74, 73, 90)
    };

    /**
     * This function chooses a rondom instrument form a hardcoded instrumentList.
     * @param random - a random generator Object
     * @return - return an Instrument object from instrumentList
     */
    public static Instrument generateRandomInstrument(Random random){
        int instrumentNumber = random.nextInt((6 - 0) + 1) + 0;
        return instrumentList[instrumentNumber];
    }

    /**
     * Generate a MIDI file for a single Channel.
     * @param c - A single channel object
     * @return - An array of MidiSequence Objects
     */
    public static ArrayList<MidiSequence> generateMidiChannel(Channel c) {
        ArrayList<MidiSequence> midiSequenceArray = new ArrayList<>();

        int timestampCounter = 0;

        StartingSequence instrument_change = new StartingSequence(192 + c.number, c.instrument, timestampCounter);
        midiSequenceArray.add(instrument_change);

        /**
         * TODO: implement the way of setting the length of the track differently (ie. a number of
         * bars so that tunes are synchronised)
         */
        for (int i = 0; i < 16; i = i+2) {
            int startingCode = generateStartingCode(c.number);
            int pitch = generatePitch(c.randomGenerator, c.instrument.rangeMin, c.instrument.rangeMax);
            int velocity = generateVelocity(c.randomGenerator);

            int duration = generateNoteDuration(c.randomGenerator);

            //create a Note on sequence (start of the note)
            Note note_on = new Note(startingCode, pitch, velocity, timestampCounter);
            timestampCounter = timestampCounter + duration;

            //create a Note off sequence (same as Note on but with velocity at 0)
            Note note_off = new Note(startingCode, pitch, 0, timestampCounter);

            midiSequenceArray.add(note_on);
            midiSequenceArray.add(note_off);
        }
        return midiSequenceArray;
    }

    /**
     * Genrates a starting code for a note. Of the form XXXX CCCC where XXXX is the code
     * of the MidiSequnce (ie. 1001 Note on) and CCCC is the channel number (from 0 to 15)
     * @param channelNumber - the number of tha channel for which the starting seqence will be generated
     * @return - a total starting code value
     */
    public static int generateStartingCode(int channelNumber){
        return 144 + channelNumber;
    }

    /**
     * Generate a pitch for a single note with constraints on type of scale, starting note
     * @param randomGenerator
     * @param rangeMin
     * @param rangeMax
     * @return
     */
    public static int generatePitch(Random randomGenerator, int rangeMin, int rangeMax){
        //Log.i("generatePitch", MessageFormat.format("Range min: {1}, Range max: {2}" , rangeMin, rangeMax));
        int randomGeneratedPitch = randomGenerator.nextInt((rangeMax - rangeMin) + 1) + rangeMin;
        int[] arrayOfPossibleNotes = completedScale(randomGenerator, randomGeneratedPitch);
        int randomStartingPoint = randomGenerator.nextInt((4 - 0) + 1 ) + 0;

        int[] randomShortArray = new int[4];
        randomShortArray[0] = arrayOfPossibleNotes[randomStartingPoint];
        randomShortArray[1] = arrayOfPossibleNotes[randomStartingPoint+1];
        randomShortArray[2] = arrayOfPossibleNotes[randomStartingPoint+2];
        randomShortArray[3] = arrayOfPossibleNotes[randomStartingPoint+3];

        for (int i : arrayOfPossibleNotes){
            Log.i("generate pitch", String.format("Note: %1$d" , i));
        }

        int randomNote = randomShortArray[randomGenerator.nextInt((3-0) + 1) + 0];
        return randomNote;
    }

    /**
     * Generate a velocity value
     * @param random_generator - a random generator object for a Channel in which note is generated
     * @return - an integer velocity value
     */
    public static int generateVelocity(Random random_generator){
        //TODO: pass signal strength as well to change the velosity value
        int rssi = -80;
        int randomGeneratedVelocity = Math.abs(rssi);

        return randomGeneratedVelocity;
    }

    /**
     * Chooses a duration for the Note
     * @param random_generator - random generator for current channel
     * @return - a duartion in miliseconds
     */
    public static int generateNoteDuration(Random random_generator){
        //TODO: make the duration fit within measures
        int[] duration = new int[] {200, 400, 400};
        int random_duration = duration[random_generator.nextInt((2-0) + 1) + 0];

        return random_duration;
    }

    public static int[] completedScale(Random randomGenerator, int startingNote){
        int[] steps = chooseScaleStep("happy");
        int[] scale = generateScale(startingNote, steps);

        return scale;
    }

    public static  int chooseStartingNote (Instrument instrument, Random randomGenerator){
        int startingNote = randomGenerator.nextInt((instrument.rangeMax -
                instrument.rangeMin) + 1) + instrument.rangeMin;

        return startingNote;
    }

    public static int[] chooseScaleStep(String emotion) {
        //return 1 for major 2 for minor
        int[] step_major = new int[] {2,2,1,2,2,2,1};
        int[] step_minor = new int[] {2,1,2,2,1,2,2};

        int[] step_array;

        if (emotion == "happy"){
            step_array = step_major;
        } else {
            step_array = step_minor;
        }
        return step_array;
    }

    public static int[] generateScale (int startingNote, int[] steps){
        int[] completedScale = new int[7];

        int stepsCounter = 0;

        for (int i = 0; i < 7; i++){
            stepsCounter = stepsCounter + steps[i];
            completedScale[i] = startingNote + stepsCounter;
        }
        return completedScale;
    }
}
