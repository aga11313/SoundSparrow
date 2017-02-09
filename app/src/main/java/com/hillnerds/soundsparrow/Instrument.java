package com.hillnerds.soundsparrow;

import java.util.Random;

/**
 * Created by aga on 02/02/17.
 */

/**
 * An instance of this class describes a specific instrument with all its necessary parameters
 */
public class Instrument {
    String instrument;
    int instrumentMidiCode;
    int rangeMin;
    int rangeMax;

    public Instrument(String instrument, int instrumentMidiCode, int rangeMin, int rangeMax){

        this.instrument = instrument;
        this.instrumentMidiCode = instrumentMidiCode;
        this.rangeMin = rangeMin;
        this.rangeMax = rangeMax;

    }

}
