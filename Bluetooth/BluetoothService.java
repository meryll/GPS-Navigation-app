package tech.hypermiles.hypermiles.Bluetooth;

import android.bluetooth.*;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.UUID;

import tech.hypermiles.hypermiles.Middleware.Logger;
import tech.hypermiles.hypermiles.Other.Constants;

/**
 * Created by Asia on 2017-01-05.
 */

public class BluetoothService {

    //for debugging
    private static final String TAG = "BluetoothService";


    // Constants that indicate the current connection state
    //todo dodac enuma
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections

    //Members
    private BluetoothAdapter mBluetoothAdapter;
    private final Handler mHandler;
    private int mState;
    private Context mContext;

    public BluetoothService(Context context, Handler handler) {
        mContext = context;
        mState = STATE_NONE;
        mHandler = handler;
        setBluetoothAdapter();
    }

    private synchronized void setState(int state) {
        Logger.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public void pairDevice(String MAC) {
        try {

            BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(MAC);

            Method method = bluetoothDevice.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(bluetoothDevice, (Object[]) null);
        } catch (Exception e) {
            Logger.e("Pair", e.getMessage());
        }
    }

    public void unpairDevice(String MAC)
    {
        try {
            BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(MAC);

            Method method = bluetoothDevice.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(bluetoothDevice, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBluetoothAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean checkIfEnabled() {
        return !(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled());
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }


    public synchronized int getState() {
        return mState;
    }

    public synchronized void start() {
        Logger.d(TAG, "start");
        setState(STATE_LISTEN);
    }


}
