package com.skydoves.magiclight_ble_control.views.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.magiclight_ble_control.Eventbus.BusProvider;
import com.skydoves.magiclight_ble_control.Eventbus.DeviceChangedEvent;
import com.skydoves.magiclight_ble_control.R;
import com.skydoves.magiclight_ble_control.bleCommunication.BluetoothGattAttributes;
import com.skydoves.magiclight_ble_control.bleCommunication.BluetoothLeService;
import com.skydoves.magiclight_ble_control.data.DeviceInfoManager;
import com.squareup.otto.Subscribe;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by skydoves on 2017-07-01.
 */

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.this.getClass().getSimpleName();

    private boolean mConnected = false;

    private BluetoothLeService mBluetoothLeService;

    private static final int REQUEST_WRITE_STORAGE = 8000;
    private static final int RESULT_LOAD_IMAGE = 8001;

    private byte[] ledrgb = new byte[3];
    private byte ledbright = (byte)0XFF;

    @Bind(R.id.colorPickerView)
    ColorPickerView colorPickerView;
    @Bind(R.id.seekBar)
    DiscreteSeekBar discreteSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        BusProvider.getInstance().register(this);

        // request ble permission
        requestPermission();

        // connection ble service
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        // set Listeners
        colorPickerView.setColorListener(colorListener);
        discreteSeekBar.setOnProgressChangeListener(progressChangeListener);
        discreteSeekBar.setMax(255);

        // connect ble device
        if (mBluetoothLeService != null)
            mBluetoothLeService.connect(DeviceInfoManager.getInstance().getDeviceAddress());
    }

    /**
     * bluetooth service connection
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Toast.makeText(getBaseContext(), R.string.ble_not_find, Toast.LENGTH_SHORT).show();
                finish();
            }
            mBluetoothLeService.connect(DeviceInfoManager.getInstance().getDeviceAddress());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    /**
     * receive connection state
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final Intent mIntent = intent;
            final String action = intent.getAction();

            // connected
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.e(TAG, "BroadcastReceiver : Connected!");
                mConnected = true;
                Toast.makeText(getBaseContext(), R.string.ble_connect_success, Toast.LENGTH_SHORT).show();
            }
            // disconnected
            else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.e(TAG, "BroadcastReceiver : Disconnected!");
                mConnected = false;
                Toast.makeText(getBaseContext(), R.string.ble_disconnected, Toast.LENGTH_SHORT).show();
            }
            // found GATT service
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.e(TAG, "BroadcastReceiver : Found GATT!");
            }
        }
    };

    /**
     * get broadcast intent-filter
     * @return
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        try {
            if (mConnected) {
                unbindService(mServiceConnection);
                mBluetoothLeService = null;
            }
        }
        catch (Exception e){
            Log.e(TAG, "BLE unbind Error");
        }
    }

    /**
     * if selected a new ble device, connect a new one
     * @param event
     */
    @Subscribe
    public void deviceChanged(DeviceChangedEvent event) {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.disconnect();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothLeService.connect(DeviceInfoManager.getInstance().getDeviceAddress());
                }
            }, 500);
        } else
            mBluetoothLeService.connect(DeviceInfoManager.getInstance().getDeviceAddress());
    }

    /**
     * send rgb byte array to ble device
     * @param rgb
     * @return
     */
    private boolean controlLed(byte[] rgb) {
        // get bluetoothGattCharacteristic
        BluetoothGattCharacteristic characteristic = mBluetoothLeService.getGattCharacteristic(BluetoothGattAttributes.LED_CHARACTERISTIC);
        if (characteristic != null) {
            // check connection
            if (!mConnected) {
                Toast.makeText(this, R.string.ble_not_connected, Toast.LENGTH_SHORT).show();
                return false;
            }

            // send characteristic data
            mBluetoothLeService.sendDataCharacteristic(characteristic, rgb);
            return true;
        }
        else
            Log.e(TAG, "Not founded characteristic");
        return false;
    }

    /**
     * colorPickerView color listener
     */
    private ColorPickerView.ColorListener colorListener = new ColorPickerView.ColorListener() {
        @Override
        public void onColorSelected(int newColor) {
            if(mConnected) {
                byte[] rgb = new byte[7];
                int color = (int)Long.parseLong(String.format("%06X", (0xFFFFFF & newColor)), 16);
                rgb[0] = (byte)0x56;
                rgb[1] = (byte)((color >> 16) & 0xFF);
                rgb[2]= (byte)((color >> 8) & 0xFF);
                rgb[3] = (byte)((color >> 0) & 0xFF);
                rgb[4] = ledbright;
                rgb[5] = (byte)0xf0;
                rgb[6] = (byte)0xaa;

                controlLed(rgb);

                for(int i=0; i<3; i++)
                    ledrgb[i] = rgb[i+1];
            }
        }
    };

    /**
     * discreteBar change listener
     */
    private DiscreteSeekBar.OnProgressChangeListener progressChangeListener = new DiscreteSeekBar.OnProgressChangeListener() {
        @Override
        public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
            if(mConnected) {
                byte[] rgb = new byte[7];
                rgb[0] = (byte)0x56;
                rgb[1] = ledrgb[0];
                rgb[2] = ledrgb[1];
                rgb[3] = ledrgb[2];
                rgb[4] = (byte)(value & 0xFF);
                rgb[5] = (byte)0x0f;
                rgb[6] = (byte)0xaa;

                controlLed(rgb);

                ledbright = (byte)(value & 0xFF);
            }
        }

        @Override
        public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
        }
    };

    @OnClick(R.id.bluetooth)
    public void btn_Bluetooth(View v) {
        Intent intent = new Intent(this, SelectDeviceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.palette)
    public void btn_Palette(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    /**
     * requestPermissions
     */
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.permission_request, Toast.LENGTH_LONG);
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_WRITE_STORAGE);
        }
    }

    /**
     * onActivityResult, request ble system enable
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // user chose not to enable bluetooth.
        if (requestCode == REQUEST_WRITE_STORAGE && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, R.string.ble_canceled, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // user choose a picture from gallery
        else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            colorPickerView.setPaletteDrawable(drawable);
        }
    }
}
