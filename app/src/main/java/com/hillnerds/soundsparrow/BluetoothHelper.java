package com.hillnerds.soundsparrow;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ParcelUuid;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by cameron on 29/01/17.
 */

public class BluetoothHelper {
    // TODO: look at whether this needs to be changed
    private final int MANF_ID = 42;

    private final int SCAN_PERIOD_MS    = 600;
    private final int AD_PERIOD_MS      = 50;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler bleHandler;
    private AdvertiseData adData;
    private AdvertiseSettings adSett;
    private Map<String, Byte> emotionToByte = new HashMap<String, Byte>();
    private String[] byteToEmotion = new String[] {"happy", "sad", "neutral", "anger"};

    private SparrowDiscoveredCallback discoveredCallback;
    private ArrayList<UUID> discovered = new ArrayList<UUID>();

    /**
     * SHOULD_SCAN - The application should scan for other devices on state change.
     * This could be either
     *  - before the application has completely initialised BLE
     *  - Whilst the application is broadcasting
     * SHOULD_ADVERTISE - The application should start advertising on state change.
     * Only occurs when the application is scanning
     */
    enum bleStates {
        SHOULD_SCAN,
        SHOULD_ADVERTISE
    }

    private bleStates currentBleState = bleStates.SHOULD_SCAN;

    /**
     * Constructor for the BluetoothHelper class. The class contains all methods
     * relating to the Bluetooth functionality of the app.
     * @param ctx   A context, not stored, but used to get a BluetoothManager object
     * @param cback Whenever a new sparrow is discovered, this callback will be invoked
     * @param uuid  The device UUID to broadcast
     */
    public BluetoothHelper(Context ctx, SparrowDiscoveredCallback cback, UUID uuid) {
        /* Because emotionToByte is a map, we need to dynamically add the elements */
        /* TODO: Do this prettier and with more emotions */
        emotionToByte.put("happy", (byte) 0);
        emotionToByte.put("sad", (byte) 1);
        emotionToByte.put("neutral", (byte) 2);
        emotionToByte.put("anger", (byte) 3);

        discoveredCallback = cback;

        /* used to stop scanning after a period of time */
        bleHandler = new Handler();

        mBluetoothAdapter = ((BluetoothManager)
                ctx.getSystemService(ctx.BLUETOOTH_SERVICE)).getAdapter();

        /* TODO: pass in the emotion dynamically */
        adData = buildAdvertiseData(uuid, "happy");
        adSett = buildAdvertiseSettings();
    }

    /**
     * Gets a UUID for the device that the application is running on.
     * This UUID will be persistent across installs of the app, and unique to each device.
     * @param ctx   A context used for access to TelephonyManager
     * @return      A UUID as above
     */
    public static UUID getDeviceUUID(Context ctx) {
        TelephonyManager tManager = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tManager.getDeviceId();

        /* UUID.fromString takes the same format that it returns with toString
           xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
           Maybe there's a better way to do this
         */
        String uuidString = deviceId + deviceId + "00";
        String correctFormatUuid = uuidString.substring(0, 8) + '-' +
                uuidString.substring(8, 12) + '-' +
                uuidString.substring(12, 16) + '-' +
                uuidString.substring(16, 20) + '-' +
                uuidString.substring(20, 32);

        return UUID.fromString(correctFormatUuid);
    }

    /**
     * This is the main entry point to the BLE functionality of the application, called whenever the
     * BLE state needs to change. All the states are described in bleStates
     *
     * Call this to start the Bluetooth part of the application running
     */
    public void bleStateMachine() {
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
                Log.wtf("BluetoothHelper", "Reached default case of bleStateMachine");
                break;
        }
    }

    /**
     * Helper function for building AdvertiseData objects. These contain the individual device
     * data that will be broadcast.
     * @param uuid  The device UUID, obtained from getDeviceUUID
     * @param emot  The emotion of the user as a string. This is then looked up in emotionToByte
     * @return      An AdvertiseData object
     */
    private AdvertiseData buildAdvertiseData(UUID uuid, String emot) {
        AdvertiseData.Builder build = new AdvertiseData.Builder();

        /* we are using service UUID to store user/device ID
         * service UUID is usually used to describe what a BLE peripheral can do
         * but since our device is not connectable we can repurpose it */
        ParcelUuid puuid = new ParcelUuid(uuid);
        build.addServiceUuid(puuid);

        byte[] emotionBytes = new byte[1];
        emotionBytes[0] = emotionToByte.get(emot);
        build.addManufacturerData(MANF_ID, emotionBytes);
        build.setIncludeDeviceName(true);

        return build.build();
    }

    /**
     * Helper function that builds an AdvertiseSettings object. Can be viewed as the canonical
     * definition of our advertisement settings.
     * @return An AdvertiseSettings object that represents the settings we want to advertise with
     */
    private AdvertiseSettings buildAdvertiseSettings() {
        AdvertiseSettings.Builder settBuild = new AdvertiseSettings.Builder();
        settBuild.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        settBuild.setConnectable(false);
        settBuild.setTimeout(500);
        settBuild.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
        
        return settBuild.build();
    }

    /**
     * Define a Callback type that will be invoked whenever another Sparrow is discovered
     */
    public interface SparrowDiscoveredCallback {
        void onSparrowDiscovered(UUID uuid, String emotion, int rssi);
    }

    /**
     * All BLE discovery and scanning goes on in here
     * Generics parameters
     * Params   - Params that doInBackground takes - Void as doesn't need any params
     * Progress - Units that publishProgress returns - Void as not implemented
     * Result   - Type that doInBackground returns - Boolean because I wasn't sure what to return
     * TODO: clean up doInBackground return
     */
    private class DiscoverTask extends AsyncTask<Void, Void, Boolean>
    {
        private ScanCallback bleScanCallback =
                new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        ScanRecord scanR = result.getScanRecord();
                        
                        SparseArray<byte[]> manfSpecData = 
                                scanR.getManufacturerSpecificData();
                        // get the first ID in the data, if our packet, there will only be 1
                        int manf_id = manfSpecData.keyAt(0);
                        if (manf_id != MANF_ID) {
                            return;
                        }
                        
                        byte[] manf_data = manfSpecData.get(manf_id);
                        UUID uuid = scanR.getServiceUuids().get(0).getUuid();
                        // if we have already seen the device with this UUID
                        if (discovered.contains(uuid)) {
                            return;
                        }
                        
                        discovered.add(uuid);
                        String emot = byteToEmotion[manf_data[0]];
                        int rssi = result.getRssi();
                        discoveredCallback.onSparrowDiscovered(uuid, emot, rssi);
                        
                        Log.i("DiscoverTask",
                                MessageFormat.format("New Device:\nrssi: {0}\naddr: {1}",
                                        rssi,
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
            },   SCAN_PERIOD_MS);
            bleScan.startScan(bleScanCallback);
            return true;
        }
    }

    private class AdvertiseTask extends AsyncTask<Void, Void, Boolean>
    {
        protected Boolean doInBackground(Void... v) {
            BluetoothLeAdvertiser bleAd = mBluetoothAdapter.getBluetoothLeAdvertiser();

            bleAd.startAdvertising(adSett, adData,
                    new AdvertiseCallback() {
                        @Override
                        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                            Log.i("AdvertiseTask", "Advertising started successfully");
                        }

                        @Override
                        public void onStartFailure(int errorCode) {
                            Log.w("AdvertiseTask", MessageFormat.format(
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
            }, AD_PERIOD_MS);

            return true;
        }
    }
}
