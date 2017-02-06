package com.hillnerds.soundsparrow;

/**
 * Created by aga on 28/01/17.
 */

public class Note extends MidiSequence {
    int pitch;
    int velocity;

    public Note(int starting_code, int pitch, int velocity, int timestamp){

        super(starting_code, timestamp);
        this.pitch = pitch;
        this.velocity = velocity;

    }

}
