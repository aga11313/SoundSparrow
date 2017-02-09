package com.hillnerds.soundsparrow;

import android.util.Log;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by aga on 06/02/17.
 */

/**
 * A class containing all the methods to do with generating MIDI Sequences for one chanel at a time
 */
public class ChannelSoundGeneration {

    //a hard coded list of all possible instruments
    private static Instrument[] instrumentList = new Instrument[] {
            new Instrument("piano", 1, 40, 60),
            new Instrument("guitar", 26, 45, 70),
            new Instrument("trombone", 58, 55, 70),
            new Instrument("trumpet", 57, 64, 80),
            new Instrument("violin", 41, 50, 65),
            new Instrument("saxophone", 66, 45, 60),
            new Instrument("flute", 74, 60, 75)
    };

     // (1100 0000) the first 4 bits of a starting sequence for an instrument change
    final static int INSTRUMENT_CHANGE_CODE = 192;

    //the amount of notes generated for each channel
    final static int CHANNEL_NOTE_AMOUNT = 16;

    // (1001 0000) the first 4 bits of a note on sequence
    final static int NOTE_ON_START_CODE = 144;

    /**
     * This function chooses a rondom instrument form a hardcoded instrumentList.
     * @param random - a random generator Object
     * @return - return an Instrument object from instrumentList
     */
    public static Instrument generateRandomInstrument(Random random){
        int instrumentNumber = random.nextInt(instrumentList.length);
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

        StartingSequence instrument_change = new StartingSequence(INSTRUMENT_CHANGE_CODE + c.number,
                c.instrument, timestampCounter);
        midiSequenceArray.add(instrument_change);

        /**
         * TODO: implement the way of setting the length of the track differently (ie. a number of
         * bars so that tunes are synchronised).
         * For now every channel is just 16 notes long
         */
        for (int noteCounter = 0; noteCounter < CHANNEL_NOTE_AMOUNT; noteCounter++) {
            int noteStartingCode = generateNoteStartingCode(c.number);
            int pitch = generatePitch(c);
            int velocity = generateVelocity(c.randomGenerator);

            int duration = generateNoteDuration(c.randomGenerator);

            //create a Note on sequence (start of the note)
            Note note_on = new Note(noteStartingCode, pitch, velocity, timestampCounter);
            timestampCounter = timestampCounter + duration;

            //create a Note off sequence (same as Note on but with velocity at 0)
            Note note_off = new Note(noteStartingCode, pitch, 0, timestampCounter);

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
    public static int generateNoteStartingCode(int channelNumber){
        return NOTE_ON_START_CODE + channelNumber;
    }

    /**
     * Generate a random pitch value for a channel based off of the scale assigned to it
     * @param c - the channel for which a note is generated
     * @return - a value between 0 and 127
     */
    public static int generatePitch(Channel c){
        int arrayIndex = c.randomGenerator.nextInt(c.scaleValues.length);
        int randomGeneratedPitch = c.scaleValues[arrayIndex];

        return randomGeneratedPitch;
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
        int random_duration = duration[random_generator.nextInt(duration.length)];

        return random_duration;
    }

    /**
     * Called from within a channel constructor.
     * Choose a note that will be a starting point for scale generation.
     * @param instrument - the instrument which will rpovide the range of pitch requirements.
     * @param randomGenerator - a random generator object
     * @return - a value within a range of the chosen instrument (between 0 and 127)
     */
    public static  int chooseStartingNote (Instrument instrument, Random randomGenerator){
        int startingNote = randomGenerator.nextInt((instrument.rangeMax -
                instrument.rangeMin) + 1) + instrument.rangeMin;

        return startingNote;
    }

    /**
     * Called from within a channel constructor.
     * Based on the emotion assigned to this channel chooses a major or minor scale and
     * its respective step array.
     * @param emotion - an emotion name
     * @return - an array of steps for an appropriate scale
     */
    public static int[] chooseScaleStep(String emotion) {
        switch (emotion){
            case "happy":
                //returns a major scale
                return new int[] {2,2,1,2,2,2,1};
            case "sad":
                //returns a minor scale
                return new int[] {2,1,2,2,1,2,2};
            default:
                return new int[] {2,2,1,2,2,2,1};
        }
    }

    /**
     * Called from within a channel constructor.
     *
     * @param startingNote - the forst note of the scale
     * @param steps - an array representing a major or minor scale
     * @return - an array of values (0 to 127) representing the allowed pitch values
     */
    public static int[] generateScale (int startingNote, int[] steps){
        int[] completedScale = new int[7];

        int stepsCounter = 0;

        for (int i = 0; i < completedScale.length; i++){
            stepsCounter = stepsCounter + steps[i];
            completedScale[i] = startingNote + stepsCounter;
        }
        return completedScale;
    }
}
