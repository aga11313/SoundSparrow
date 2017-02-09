package com.hillnerds.soundsparrow;

/**
 * Created by aga on 02/02/17.
 */

/**
 * An instance of this class represents a particular starting sequence in a MIDI format.
 * Describes the type of special events such as an instrument change sequences.
 */
public class StartingSequence extends MidiSequence {
    Instrument instrument;

    public StartingSequence (int startingCode, Instrument instrument, int timestamp){
        super(startingCode, timestamp);
        this.instrument = instrument;
    }
}
