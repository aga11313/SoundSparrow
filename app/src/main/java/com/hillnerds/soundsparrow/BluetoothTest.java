package com.hillnerds.soundsparrow;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.security.Permission;
import java.text.MessageFormat;

public class BluetoothTest extends AppCompatActivity {
    private String[] requested = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
    };

    private BluetoothAdapter mBluetoothAdapter;
    private Handler bleHandler;

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

        bleHandler = new Handler();

        mBluetoothAdapter = ((BluetoothManager)
                getSystemService(this.BLUETOOTH_SERVICE)).getAdapter();

        AskPermissions();

        DiscoverTask d = new DiscoverTask();
        d.execute();
    }

    protected void AskPermissions() {
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
                }
            }, 1000);
            bleScan.startScan(bleScanCallback);
            return true;
        }
    }
}
