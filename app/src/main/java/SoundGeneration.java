/**
 * Created by aga on 28/01/17.
 */

public class SoundGeneration {

    int[][] midi_file;
    String[] instrument_list = new String[] {"piano", "guitar", "drums", "trombone", "trumpet", "violin", "saxophone", "flute"};
    int[] instrument_list_midi_codes = new int[] {1,26,115,58,57,41,66,74};

    public void chooseEmotion () {

        return;
    }

    public int chooseStartingNote(){


        return 0;
    }

    public int[] chooseTypeOfScale(int emotion) {

        //return 1 for major 2 for minor and 3 for b major???
        int[] step_major = new int[] {2,2,1,2,2,2,1};
        int[] step_minor = new int[] {2,1,2,2,1,2,2};

        int[] step_array = step_major;

        return step_array;
    }

    public int[] generateScale (int starting_note, int[] steps){

        int[] scale_generated = new int[] {0,0};
        //scale_generated =

        return scale_generated;
    }

}
