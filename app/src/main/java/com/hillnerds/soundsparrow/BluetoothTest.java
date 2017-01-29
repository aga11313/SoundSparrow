package com.hillnerds.soundsparrow;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BluetoothTest extends AppCompatActivity {
    private String[] requested = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
    };

    private BluetoothAdapter mBluetoothAdapter;
    private Handler bleHandler;
    enum bleStates {
        SHOULD_SCAN,
        SHOULD_ADVERTISE
    }
    private bleStates currentBleState = bleStates.SHOULD_SCAN;
    private AdvertiseData adData;
    private final int MANF_ID = 42;
    private Map<String, Byte> emotionToByte = new HashMap<String, Byte>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        emotionToByte.put("happy", (byte) 1);
        emotionToByte.put("sad", (byte) 2);
        emotionToByte.put("neutral", (byte) 3);
        emotionToByte.put("anger", (byte) 4);

        bleHandler = new Handler();

        mBluetoothAdapter = ((BluetoothManager)
                getSystemService(this.BLUETOOTH_SERVICE)).getAdapter();

        askPermissions();

        adData = buildAdvertisingData(UUID.randomUUID(), "happy");

        bleStateMachine();
    }

    private void bleStateMachine() {
        switch (currentBleState)
        {
            case SHOULD_SCAN:
                currentBleState = bleStates.SHOULD_ADVERTISE;
                DiscoverTask d = new DiscoverTask();
                d.execute();
                break;
            case SHOULD_ADVERTISE:
                currentBleState = bleStates.SHOULD_SCAN;
                AdvertiseTask a = new AdvertiseTask();
                a.execute();
                break;
            default:
                // not actually expected to reach here
                break;
        }
    }

    protected void askPermissions() {
        int idx = 0;
        for (String permission : requested) {
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    permission);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
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

    private AdvertiseData buildAdvertisingData(UUID uuid, String emot) {
        AdvertiseData.Builder build = new AdvertiseData.Builder();

        // we are using service UUID for user/device ID
        ParcelUuid puuid = new ParcelUuid(uuid);
        build.addServiceUuid(puuid);

        byte[] emotionBytes = new byte[1];
        emotionBytes[0] = emotionToByte.get(emot);
        build.addManufacturerData(MANF_ID, "emot".getBytes());
        build.addManufacturerData(MANF_ID, emotionBytes);

        return build.build();
    }

    private class DiscoverTask extends AsyncTask<Void, Void, Boolean>
    {
        private ScanCallback bleScanCallback =
                new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        Log.i("DiscoverTask",
                                MessageFormat.format("New Device:\nrssi: {0}\naddr: {1}",
                                        result.getRssi(),
                                        result.getDevice().getAddress()));
                    }
                };

        protected Boolean doInBackground(Void... v) {
            final BluetoothLeScanner bleScan = mBluetoothAdapter.getBluetoothLeScanner();
            bleHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bleScan.stopScan(bleScanCallback);
                    bleStateMachine();
                }
            }, 1000);
            bleScan.startScan(bleScanCallback);
            return true;
        }
    }

    private class AdvertiseTask extends AsyncTask<Void, Void, Boolean>
    {
        protected Boolean doInBackground(Void... v) {
            BluetoothLeAdvertiser bleAd = mBluetoothAdapter.getBluetoothLeAdvertiser();

            AdvertiseSettings.Builder settBuild = new AdvertiseSettings.Builder();
            settBuild.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
            settBuild.setConnectable(false);
            settBuild.setTimeout(500);
            settBuild.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);

            bleAd.startAdvertising(settBuild.build(), adData,
                    new AdvertiseCallback() {
                        @Override
                        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                            Log.i("AdvertiseTask", "Advertising started successfully");
                        }

                        @Override
                        public void onStartFailure(int errorCode) {
                            Log.wtf("AdvertiseTask", MessageFormat.format(
                                    "Advertising failed to start with code {0}",
                                    errorCode
                            ));
                        }
                    });

            bleHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bleStateMachine();
                }
            }, 500);

            return true;
        }
    }
}
