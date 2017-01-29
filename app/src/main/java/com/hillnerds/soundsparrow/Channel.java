package com.hillnerds.soundsparrow;

import java.util.Random;

/**
 * Created by aga on 28/01/17.
 */

public class Channel {

    int id;
    int emotion;
    int number;
    int instrument;
    int range_min;
    int range_max;

    public Channel (int id, int emotion, int number, int instrument){

        this.id = id;
        this.emotion = emotion;
        this.number = number;
        this.instrument = instrument;

    }

    public Channel (){


    }

    public void generateInstrument(Random random){

        //int instrument = random.nextInt((7 - 0) + 1) + 0;
        int instrument  = 0;

        this.instrument = SoundGeneration.instrument_list_midi_codes[instrument];

        int[] range_array = SoundGeneration.instrument_range_list[instrument];

        range_min = range_array[0];
        range_max = range_array[1];

    }

}