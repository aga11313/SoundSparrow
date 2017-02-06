package com.hillnerds.soundsparrow;

import java.util.Random;

/**
 * Created by aga on 28/01/17.
 */

public class Channel {

    long uuid;
    String emotion;
    int number;
    Instrument instrument;
    Random randomGenerator;
    //String scaleName; //major or minor
    int[] steps; //dependant on the choice of minor or major
    int startingNote;
    int[] scaleValues;

    public Channel (long uuid, String emotion, int number){

        this.uuid = uuid;
        this.emotion = emotion;
        this.number = number;
        this.instrument = instrument;

        this.randomGenerator = new Random(uuid);

        this.instrument = ChannelSoundGeneration.generateRandomInstrument(this.randomGenerator);
        this.steps = ChannelSoundGeneration.chooseScaleStep(this.emotion);
        this.startingNote = ChannelSoundGeneration.chooseStartingNote(this.instrument, this.randomGenerator);
        this.scaleValues = ChannelSoundGeneration.generateScale(this.startingNote, this.steps);

    }


}
