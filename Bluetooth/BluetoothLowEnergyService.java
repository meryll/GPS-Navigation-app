package tech.hypermiles.hypermiles.Bluetooth;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.UUID;

import tech.hypermiles.hypermiles.Middleware.Logger;
import tech.hypermiles.hypermiles.Middleware.SavedDataManager;

/**
 * Created by Asia on 2017-01-10.
 */

public class BluetoothLowEnergyService {

    private static final String TAG = "BluetoothLeService";
    // Unique UUID for this application
    private static final UUID DEVICE_UUID = UUID.fromString("00001234-0000-1000-8000-00805f9b34fb");
    private static final UUID WRITE_CHARACTERISTICS_UUID = UUID.fromString("00001235-0000-1000-8000-00805f9b34fb");
    private static final UUID READ_CHAR_UUID = UUID.fromString("00001236-0000-1000-8000-00805f9b34fb");
    private static final UUID DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    //todo dodac oblsuge stanow
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    private Context mContext;
    private Handler mHandler;
    private int mState;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private static BluetoothGatt mBluetoothGatt;
    private static String mBluetoothDeviceAddress;
    private static BluetoothGattCharacteristic write_charc;

    private SavedDataManager mSavedDataManager;
    private TextView mBluetoothTextView;

    public BluetoothLowEnergyService(Context context, Handler handler, FragmentActivity activity, TextView infoText)
    {
        mContext = context;
        mSavedDataManager =  new SavedDataManager(activity);
        mHandler = handler;
        mBluetoothTextView = infoText;
        setBluetoothAdapter();
    }

    private void setBluetoothAdapter()
    {
        BluetoothManager bluetoothManager = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    private synchronized void setState(int state) {
        Logger.d(TAG, "setState() " + mState + " -> " + state);
        setText(mState+"");
        mState = state;
       // mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public Boolean connect(String MAC) {

        Logger.d(TAG, "connect to: " + MAC);

        if (mBluetoothAdapter == null || MAC == null) {
            Logger.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        if (mBluetoothDeviceAddress != null && MAC.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Logger.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                setState(STATE_CONNECTING);
                mSavedDataManager.setMacAddressValue(mBluetoothDeviceAddress);
                sendMessage();
                return true;
            } else {
                return false;
            }
        }

        mDevice = mBluetoothAdapter.getRemoteDevice(MAC);

        if (mDevice == null) {
            Logger.i(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        mBluetoothGatt  = mDevice.connectGatt(mContext, true, mGattCallback);
        Logger.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = MAC;
        setState(STATE_CONNECTING);
        return true;
    }

    private void setText(String text)
    {
        if(mBluetoothTextView==null) return;

        mBluetoothTextView.setText(text);
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Logger.i(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothTextView.setText("Disconnected");
        mBluetoothGatt.disconnect();
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public boolean checkIfAvailable()
    {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public boolean checkIfEnabled()
    {
        return !(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled());
    }

    private PackageManager getPackageManager()
    {
        return mContext.getPackageManager();
    }

    public static Boolean sendMessage()
    {
        if(write_charc==null) {
//            Logger.i(TAG, "BLE not initialized.");
            return false;
        }
        byte[] value = tech.hypermiles.hypermiles.Bluetooth.Message.get();
        write_charc.setValue(value);
        boolean status = mBluetoothGatt.writeCharacteristic(write_charc);
        Logger.i(TAG, "Message sent "+status);

        return status;
    }

        private final BluetoothGattCallback mGattCallback =
                new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                        int newState) {
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            Logger.i(TAG, "Connected to GATT server.");
                            Logger.i(TAG, "Attempting to start service discovery:");
                            mBluetoothGatt.discoverServices();

                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            Logger.i(TAG, "Disconnected from GATT server.");
                        }
                        Logger.i(TAG,"New state "+newState);
                    }

                    @Override
                    // New services discovered
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            Logger.i(TAG, "GATT_SUCCESS, services discovered)");
                            BluetoothGattService service = gatt.getService(DEVICE_UUID);
                            write_charc = service.getCharacteristic(WRITE_CHARACTERISTICS_UUID);
//                            BluetoothGattCharacteristic read_charc = service.getCharacteristic(READ_CHAR_UUID);
//                            setCharacteristicNotification(read_charc, true);
                            sendMessage();

                        } else {
                            Logger.w(TAG, "onServicesDiscovered received: " + status);

                        }
                    }

                    @Override
                    public void onCharacteristicChanged(BluetoothGatt gatt,
                                                        BluetoothGattCharacteristic characteristic) {

                        Logger.w(TAG, "characteristic changed");
//                        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    }

                    @Override
                    // Result of a characteristic read operation
                    public void onCharacteristicRead(BluetoothGatt gatt,
                                                     BluetoothGattCharacteristic characteristic,
                                                     int status) {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            Logger.w(TAG, "action_data_available");
//                            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                        }
                    }

                    @Override
                    // Result of a characteristic read operation
                    public void onCharacteristicWrite(BluetoothGatt gatt,
                                                      BluetoothGattCharacteristic characteristic,
                                                      int status) {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            Logger.w(TAG, "data wrote");
//                            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                        } else {
                            Logger.w(TAG, "Failed to write data "+status);
                        }
                    }

                    @Override
                    // Result of a characteristic read operation
                    public void onDescriptorWrite(BluetoothGatt gatt,
                                                  BluetoothGattDescriptor descriptor,
                                                  int status) {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            Logger.w(TAG, "descrptor wrote");
//                            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                        } else {
                            Logger.w(TAG, "Failed to write descrptor "+status);
                        }
                    }

                    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
                        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                            Logger.w(TAG, "BluetoothAdapter not initialized");
                            return;
                        }
                        mBluetoothGatt.readCharacteristic(characteristic);
                    }

                    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                                              boolean enabled) {
                        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                            Logger.w(TAG, "BluetoothAdapter not initialized");
                            return;
                        }
                        mBluetoothGatt.setCharacteristicNotification(characteristic, true);

                        // This is specific to Heart Rate Measurement.
                        if (READ_CHAR_UUID.equals(characteristic.getUuid())) {
                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESCRIPTOR_UUID);
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            mBluetoothGatt.writeDescriptor(descriptor);
                        }
                    }
                };
}
