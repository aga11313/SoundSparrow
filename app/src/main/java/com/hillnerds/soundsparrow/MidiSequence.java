package com.hillnerds.soundsparrow;

/**
 * Created by aga on 02/02/17.
 */

public class MidiSequence {
    int timestamp;
    int startingCode;

    public MidiSequence(int startingCode, int timestamp){
        this.timestamp = timestamp;
        this.startingCode = startingCode;
    }

}