package com.hillnerds.soundsparrow;

import java.util.Random;

/**
 * Created by aga on 02/02/17.
 */

public class Instrument {
    String instrument;
    int instrumentMidiCode;
    int rangeMin;
    int rangeMax;

    public static String[] instrument_list = new String[] {"piano", "guitar", "trombone", "trumpet", "violin", "saxophone", "flute"};
    public static int[] instrument_list_midi_codes = new int[] {1,26,58,57,41,66,74};
    public static int[][] instrument_range_list = new int[][] {{40, 60},{45, 70},{55, 70},{64, 80},{50,65},{45, 60},{73, 90}};

    public Instrument(String instrument, int instrumentMidiCode, int rangeMin, int rangeMax){

        this.instrument = instrument;
        this.instrumentMidiCode = instrumentMidiCode;
        this.rangeMin = rangeMin;
        this.rangeMax = rangeMax;

    }

}
