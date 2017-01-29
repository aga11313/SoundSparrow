package com.hillnerds.soundsparrow;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ParcelUuid;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by cameron on 29/01/17.
 */

public class BluetoothHelper {
    private BluetoothAdapter mBluetoothAdapter;
    private Handler bleHandler;
    enum bleStates {
        SHOULD_SCAN,
        SHOULD_ADVERTISE
    }
    private bleStates currentBleState = bleStates.SHOULD_SCAN;
    private AdvertiseData adData;
    private AdvertiseSettings adSett;
    private final int MANF_ID = 42;
    private Map<String, Byte> emotionToByte = new HashMap<String, Byte>();
    private String[] byteToEmotion = new String[] {"happy", "sad", "neutral", "anger"};

    private SparrowDiscoveredCallback discoveredCallback;

    public BluetoothHelper(Context ctx, SparrowDiscoveredCallback cback, UUID uuid) {
        emotionToByte.put("happy", (byte) 0);
        emotionToByte.put("sad", (byte) 1);
        emotionToByte.put("neutral", (byte) 2);
        emotionToByte.put("anger", (byte) 3);

        discoveredCallback = cback;

        bleHandler = new Handler();

        mBluetoothAdapter = ((BluetoothManager)
                ctx.getSystemService(ctx.BLUETOOTH_SERVICE)).getAdapter();

        adData = buildAdvertiseData(uuid, "happy");
        adSett = buildAdvertiseSettings();
    }

    public static UUID getDeviceUUID(Context ctx) {
        TelephonyManager tManager = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tManager.getDeviceId();

        // 32 characters so that correct number of hex octets
        String uuidString = deviceId + deviceId + "00";
        String correctFormatUuid = uuidString.substring(0, 8) + '-' +
                uuidString.substring(8, 12) + '-' +
                uuidString.substring(12, 16) + '-' +
                uuidString.substring(16, 20) + '-' +
                uuidString.substring(20, 32);

        return UUID.fromString(correctFormatUuid);
    }
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
                // not actually expected to reach here
                break;
        }
    }

    private AdvertiseData buildAdvertiseData(UUID uuid, String emot) {
        AdvertiseData.Builder build = new AdvertiseData.Builder();

        // we are using service UUID for user/device ID
        ParcelUuid puuid = new ParcelUuid(uuid);
        build.addServiceUuid(puuid);

        byte[] emotionBytes = new byte[1];
        emotionBytes[0] = emotionToByte.get(emot);
        build.addManufacturerData(MANF_ID, emotionBytes);
        build.setIncludeDeviceName(true);

        return build.build();
    }
    
    private AdvertiseSettings buildAdvertiseSettings() {
        AdvertiseSettings.Builder settBuild = new AdvertiseSettings.Builder();
        settBuild.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        settBuild.setConnectable(false);
        settBuild.setTimeout(500);
        settBuild.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
        
        return settBuild.build();
    }

    public interface SparrowDiscoveredCallback {
        void onSparrowDiscovered(UUID uuid, String emotion, int rssi);
    }

    private class DiscoverTask extends AsyncTask<Void, Void, Boolean>
    {
        private ScanCallback bleScanCallback =
                new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        SparseArray<byte[]> b = result.getScanRecord().getManufacturerSpecificData();
                        int manf_id = b.keyAt(0);
                        byte[] manf_data = b.get(manf_id);
                        if (manf_id != MANF_ID) {
                            return;
                        }
                        UUID uuid = result.getScanRecord().getServiceUuids().get(0).getUuid();
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
            }, 4000);
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
            }, 100);

            return true;
        }
    }
}
