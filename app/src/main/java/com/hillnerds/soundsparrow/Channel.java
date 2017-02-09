package com.hillnerds.soundsparrow;

import java.util.Random;
import java.util.UUID;

/**
 * Created by aga on 28/01/17.
 */

/**
 * A class whose instances represent one of 15 MIDI channels that can be created.
 */
public class Channel {

    UUID uuid;
    String emotion;
    int number;
    Instrument instrument;
    Random randomGenerator;
    int[] steps; //dependant on the choice of minor or major
    int startingNote;
    int[] scaleValues;

    public Channel (UUID uuid, String emotion, int number){

        this.uuid = uuid;
        this.emotion = emotion;
        this.number = number;

        this.randomGenerator = new Random(uuid.getLeastSignificantBits());

        this.instrument = ChannelSoundGeneration.generateRandomInstrument(this.randomGenerator);
        this.steps = ChannelSoundGeneration.chooseScaleStep(this.emotion);
        this.startingNote = ChannelSoundGeneration.chooseStartingNote(this.instrument, this.randomGenerator);
        this.scaleValues = ChannelSoundGeneration.generateScale(this.startingNote, this.steps);

    }


}
