package com.hillnerds.soundsparrow;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;

/**
 * Created by cameron on 28/01/17.
 */

/* Class will contain all functionality related to discovering other devices
 * and broadcasting the device itself
 */
public class DiscoveryHelper {
    private BluetoothAdapter mBluetoothAdapter;
    private Activity ctx;
    // unique identifier for intents
    private final int REQUEST_ENABLE_BT = 42;

    public DiscoveryHelper(Activity act_ctx) {
        ctx = act_ctx;
        final BluetoothManager bluetoothManager =
                (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        EnableBluetooth();
    }

    public void EnableBluetooth() {
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ctx.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}
