package com.hillnerds.soundsparrow;

/**
 * Created by aga on 28/01/17.
 */

public class Note {
    int starting_code;
    int pitch;
    int velocity;
    byte[] params;
    int timestamp;
    int instrument;
    boolean isStartingBit;

    public Note(int starting_code, int pitch, int velocity, int timestamp){
        params = new byte[3];

        this.starting_code = starting_code;
        this.pitch = pitch;
        this.velocity = velocity;
        this.timestamp = timestamp;
        this.params[0] = (byte)starting_code;
        this.params[1] = (byte)starting_code;
        this.params[2] = (byte)starting_code;
        this.isStartingBit = false;
    }

    public Note(int starting_code, int instrument, int timestamp){
        this.starting_code = starting_code;
        this.instrument = instrument;
        this.isStartingBit = true;
        this.timestamp = timestamp;
    }
}
