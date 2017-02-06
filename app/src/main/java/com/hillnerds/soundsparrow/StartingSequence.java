package com.hillnerds.soundsparrow;

/**
 * Created by aga on 02/02/17.
 */

public class StartingSequence extends MidiSequence {
    Instrument instrument;

    public StartingSequence (int startingCode, Instrument instrument, int timestamp){
        super(startingCode, timestamp);
        this.instrument = instrument;
    }
}
