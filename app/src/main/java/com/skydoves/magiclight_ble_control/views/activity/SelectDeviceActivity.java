package com.skydoves.magiclight_ble_control.views.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skydoves.magiclight_ble_control.otto.BusProvider;
import com.skydoves.magiclight_ble_control.otto.DeviceChangedEvent;
import com.skydoves.magiclight_ble_control.R;
import com.skydoves.magiclight_ble_control.data.DeviceInfoManager;
import com.skydoves.magiclight_ble_control.views.adapter.LeDeviceListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by skydoves on 2017-07-01.
 */

public class SelectDeviceActivity extends Activity {

    public boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private LeDeviceListAdapter mLeDeviceListAdapter;

    private ArrayList<String> deviceList;

    private static final long SCAN_PERIOD = 5000;
    private static final int REQUEST_ENABLE_BT = 10000;

    private Handler mHandler;

    @Bind(R.id.bluetoothlist_listview_devices)
    ListView listView_BluetoothList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bluetoothlist);
        this.setFinishOnTouchOutside(false);
        ButterKnife.bind(this);

        mHandler = new Handler();

        // indicate scanning in the title
        TextView status = (TextView) findViewById(R.id.bluetoothlist_status);
        status.setText(R.string.ble_startfind);

        // initialize bluetooth manager & adapter
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        // if bluetooth is not currently enabled,
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else {
            // initialize list view adapter
            deviceList = new ArrayList();
            mLeDeviceListAdapter = new LeDeviceListAdapter(this, R.layout.item_bluetoothdevice);
            listView_BluetoothList.setAdapter(mLeDeviceListAdapter);
            listView_BluetoothList.setOnItemClickListener(new ListViewItemClickListener());
            scanLeDevice(true);
        }
    }

    /**
     * scan Le devices
     * @param enable
     */
    //region
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothLeScanner.startScan(mScanCallback);
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(mScanCallback);
        }
    }

    /**
     * scan result call back
     */
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            processResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                processResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Toast.makeText(getApplicationContext(), R.string.ble_not_find, Toast.LENGTH_SHORT).show();
        }

        private void processResult(final ScanResult result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String deviceName = result.getScanRecord().getDeviceName();
                    if(deviceName != null && !deviceName.equals("") && !deviceList.contains(deviceName)) {
                        mLeDeviceListAdapter.addDevice(result.getDevice());
                        deviceList.add(deviceName);
                    }
                }
            });
        }
    };
    //endregion

    /**
     * listViewItem click listener
     */
    private class ListViewItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View clickedView, int pos, long id) {
            BluetoothDevice device = mLeDeviceListAdapter.getDevice(pos);
            if (device == null) return;

            DeviceInfoManager manager = DeviceInfoManager.getInstance();
            manager.setDeviceInfo(device);
            BusProvider.getInstance().post(new DeviceChangedEvent());

            Toast.makeText(getBaseContext(), R.string.ble_selected, Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    /**
     * closed activity with onBackPressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, R.string.ble_canceldfind, Toast.LENGTH_SHORT).show();
    }

    /**
     * onActivityResult, request ble system enable
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // user chose not to enable bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, R.string.ble_canceled, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // user chose enable bluetooth
        else if(requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK){
            finish();
            Intent intent = new Intent(this, SelectDeviceActivity.class);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
