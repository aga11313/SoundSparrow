package com.hillnerds.soundsparrow;

import android.util.Log;

import java.util.Random;

/**
 * Created by aga on 28/01/17.
 */

public class SoundGeneration {




    int[][] midi_file;
    public static String[] instrument_list = new String[] {"piano", "guitar", "trombone", "trumpet", "violin", "saxophone", "flute"};
    public static int[] instrument_list_midi_codes = new int[] {1,26,58,57,41,66,74};
    public static int[][] instrument_range_list = new int[][] {{40, 60},{45, 70},{55, 70},{64, 80},{50,65},{45, 60},{73, 90}};

    int number_of_channels = 4;

    /*public void initialize_random () {

        int random_seed_num = (int) Integer.parseInt(randomSeed);
        Random randomGenerator = new Random(random_seed_num);

    }*/

    public static int generate_starting_code(int number){

        return 144 + number;

    }

    public static int generate_pitch(Random random_generator, int range_min, int range_max){

        int random_generated_pitch = random_generator.nextInt((range_max - range_min) + 1) + range_min;

        int[] array_of_possible_notes = completed_scale(random_generator, random_generated_pitch);

        int random_starting_point = random_generator.nextInt((4 - 0) +1 ) + 0;

        int[] random_short_array = new int[4];
        random_short_array[0] = array_of_possible_notes[random_starting_point];
        random_short_array[1] = array_of_possible_notes[random_starting_point+1];
        random_short_array[2] = array_of_possible_notes[random_starting_point+2];
        random_short_array[3] = array_of_possible_notes[random_starting_point+3];


        for (int i : array_of_possible_notes){

            //Log.i("generate pitch", String.format("Note: %1$d" , i));

        }

        int random_note = random_short_array[random_generator.nextInt((3-0) + 1) + 0];

        return random_note;

    }

    public static int generate_velocity(Random random_generator){

        //pass signal strength as well
        //int random_generated_velocity = randomGenerator.nextInt((100 - 80) + 1) + 80;

        int wifi = -80;

        int random_generated_velocity = Math.abs(wifi);

        return random_generated_velocity;

    }

    public static int generate_note_duration(Random random_generator){

        int[] duration = new int[] {200, 400, 400};

        int random_duration = duration[random_generator.nextInt((2-0) + 1) + 0];

        //random_duration = 200;

        return random_duration;

    }

    public static int[] completed_scale(Random random_generator, int starting_note){

        int[] steps = chooseTypeOfScale("happy");
        int[] scale = generateScale(starting_note, steps);

        return scale;

    }

    public void chooseEmotion () {

        return;
    }

    public int chooseStartingNote(){


        return 0;
    }

    public static int[] chooseTypeOfScale(String emotion) {

        //return 1 for major 2 for minor and 3 for b major???
        int[] step_major = new int[] {2,2,1,2,2,2,1};
        int[] step_minor = new int[] {2,1,2,2,1,2,2};

        int[] step_array = new int[78];

        if (emotion == "happy"){
            step_array = step_major;
        } else if (emotion == "sad") {
            step_array = step_minor;
        } else {
            step_array = step_major;
        }

        return step_array;
    }

    public static int[] generateScale (int starting_note, int[] steps){

        int[] scale_generated = new int[8];

        int steps_counter = 0;

        for (int i = 0; i < 7; i++){
            steps_counter = steps_counter + steps[i];

            scale_generated[i] = starting_note + steps_counter;
            Log.i("parse_midi_file", String.format("Scale generated: %1$d" , scale_generated[i]));

        }

        return scale_generated;
    }

}
