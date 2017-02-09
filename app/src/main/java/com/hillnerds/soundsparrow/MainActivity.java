package com.hillnerds.soundsparrow;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    /**
     * TODO: Update these to match all of the permissions in the manifest
     */
    private String[] requested = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    };

    public static UUID uuid;
    public String uuid_str;
    public static long uuid_long;

    private String imageStoreLocation;

    private final String PREF_NAME = "SparrowPreferences";
    private SharedPreferences appPref;
    private TextView greetings;
    private static final int REQUEST_TAKE_PHOTO = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        greetings = (TextView) findViewById(R.id.greeting);

        askPermissions();

        if(appPref.getString("user_name", "") == "") {
            askQuestions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        greetings.setText("Hi, " + appPref.getString("user_name", ""));

        if(appPref.getString("current_emotion", "") != "") {
            ImageButton playButton = (ImageButton) findViewById(R.id.play_button);
            playButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                EmotionService es = new EmotionService(this);
                es.execute(imageStoreLocation);
            }
        } else {
            Log.wtf("MainActivity", MessageFormat.format(
                    "Result returned from activity with code {0}", requestCode));
        }
    }

    public void openSound(View view) {
        Intent intent = new Intent(this, Sound.class);
        startActivity(intent);
    }

    public void editDataClick(View view) {
        askQuestions();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                timeStamp,
                ".jpg",
                storageDir
        );
    }

    public void newEmotionClick(View view) {
        Intent takeAPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure activity present to take photo
        if (takeAPicture.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                imageStoreLocation = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                Log.e("MainActivity", "Error occurred whilst creating image file");
            }
            if (photoFile == null) {
                Toast.makeText(this, "Couldn't create an image file", Toast.LENGTH_LONG).show();
                return;
            }
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.hillnerds.soundsparrow.fileprovider",
                    photoFile);
            takeAPicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takeAPicture, REQUEST_TAKE_PHOTO);
        }
    }

    public void askQuestions() {
        Intent intent = new Intent(this, Questions.class);

        startActivity(intent);
    }

    private void askPermissions() {
        int idx = 0;
        for (String permission : requested) {
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    permission);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.i("Permissions", MessageFormat.format("Requesting permission {0}", permission));
                ActivityCompat.requestPermissions(this,
                        new String[] {permission},
                        idx);
            }
            idx++;
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length == 0
                || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,
                    MessageFormat.format("Permission {0} not granted", requested[requestCode]),
                    Toast.LENGTH_LONG);
        }
    }
}
