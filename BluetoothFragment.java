package tech.hypermiles.hypermiles;

import android.support.v4.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import tech.hypermiles.hypermiles.Bluetooth.BluetoothLowEnergyService;
import tech.hypermiles.hypermiles.Bluetooth.BluetoothService;
import tech.hypermiles.hypermiles.Exceptions.DefaultUncaughtExceptionHandler;
import tech.hypermiles.hypermiles.Middleware.Logger;

public class BluetoothFragment extends Fragment {

//    private final static UUID APP_UUID = UUID.fromString("fc5ffc49-00e3-4c8b-9cf1-6b72aad1001a");
//    private final static String APP_NAME = "HypermilesTest";

    private static final String TAG = "BluetoothFragment";

    private final int REQUEST_ENABLE_BT = 6;
    private final int DISCOVERABLE_BT_REQUEST_CODE = 3;
    private final int DISCOVERABLE_DURATION = 120;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;

    private boolean mScanning;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    BluetoothService mBluetoothService;
    BluetoothLowEnergyService mBluetoothLowEnergyService;

    Button getDevicesButton;

    private ArrayAdapter<String> allDevicesArrayAdapter;
    private ArrayAdapter<String> pairedDevicesArrayAdapter;

    private ListView allDevicesListView;
    private ListView pairedDevicesListView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //----------------
        Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());

    }

    @Override
    public void onStart() {
        super.onStart();

        setupServices();
        makeDiscoverable();
        checkIfEnabledAndAvailable();
        findDevices();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try
        {
            getActivity().unregisterReceiver(bReceiver);
        }
        catch (Exception e)
        {
            Logger.d("BluetoothAdapter", e.getMessage());
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBluetoothService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mBluetoothService.start();
            }
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.paired_device_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        String MAC = getMacAddress(pairedDevicesListView, position);

        switch (item.getItemId()) {
            case R.id.connectButton:
                return mBluetoothLowEnergyService.connect(MAC);
            case R.id.unpairButton:
                mBluetoothService.unpairDevice(MAC);
                getPairedBluetoothDevices();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        setDevicesButton(view);
        setAllDevicesList(view);
        setPairedDevicesList(view);

        registerForContextMenu(pairedDevicesListView);
        return view;
    }


    private void setupServices() {
        Logger.d(TAG, "Setup services.");
        mBluetoothService = new BluetoothService(getActivity(), mHandler);
        mBluetoothLowEnergyService = new BluetoothLowEnergyService(getActivity(), mHandler, getActivity(), null);
    }


    private void setDevicesButton(View view) {
        Logger.d(TAG, "Set device button.");
        getDevicesButton = (Button) view.findViewById(R.id.getDevices);
        getDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findDevices();
            }
        });
    }

    private void checkIfEnabledAndAvailable() {
        Logger.d(TAG, "Check if enabled and available");
        if (!mBluetoothService.checkIfEnabled() || !mBluetoothLowEnergyService.checkIfEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (!mBluetoothLowEnergyService.checkIfAvailable()) {
            Logger.d("BluetoothAdapter", "bluetooth low energy is not supported");
        }
    }

    private void setAllDevicesList(View view) {
        Logger.d(TAG, "Set all devices list.");
        allDevicesListView = (ListView) view.findViewById(R.id.devicesList);
        String cars[] = {};
        ArrayList<String> carL = new ArrayList<String>();
        carL.addAll(Arrays.asList(cars));
        allDevicesArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.bluetooth_row, carL);
        allDevicesListView.setAdapter(allDevicesArrayAdapter);

        allDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String MAC = getMacAddress(allDevicesListView, position);
                mBluetoothService.pairDevice(MAC);
                getPairedBluetoothDevices();
            }
        });
    }

    private String getMacAddress(ListView listView, int position)
    {
        String itemValue = (String) listView.getItemAtPosition(position);
        return itemValue.substring(itemValue.length() - 17);
    }

    private void setPairedDevicesList(View view) {
        pairedDevicesListView = (ListView) view.findViewById(R.id.paieredDevicesList);
        String paired[] = {"none"};
        ArrayList<String> pairedL = new ArrayList<String>();
        pairedL.addAll(Arrays.asList(paired));
        pairedDevicesArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.bluetooth_row, pairedL);
        pairedDevicesListView.setAdapter(pairedDevicesArrayAdapter);
    }

    protected void makeDiscoverable() {
        // Make local device discoverable
        if(!mBluetoothService.checkIfEnabled())
        {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
            startActivityForResult(discoverableIntent, DISCOVERABLE_BT_REQUEST_CODE);
        }
    }


    void getPairedBluetoothDevices() {

        //todo przeniesc do BluetoothService
        Set<BluetoothDevice> pairedDevices = mBluetoothService.getBluetoothAdapter().getBondedDevices();
// If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                pairedDevicesArrayAdapter.notifyDataSetChanged();
                Logger.d("Paired device found", device.getName() + "\n" + device.getAddress());
            }
        }
    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                allDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                allDevicesArrayAdapter.notifyDataSetChanged();
            }

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    showToast("Paired");
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    showToast("Unpaired");
                }

            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {


            }
        }
    };

    private void showToast(String text) {
        Context context = getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void findDevices() {
        if (mBluetoothService.getBluetoothAdapter().isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            mBluetoothService.getBluetoothAdapter().cancelDiscovery();
        } else {
//            allDevicesArrayAdapter.clear();
            pairedDevicesArrayAdapter.clear();
//            scanLeDevice(true);

            getPairedBluetoothDevices();
            mBluetoothService.getBluetoothAdapter().startDiscovery();
            getActivity().registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            getActivity().registerReceiver(bReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        }
    }


    //todo czy jest potrzebne?
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Logger.i("MHandler", Integer.toString(msg.what));

            FragmentActivity activity = getActivity();
//            switch (msg.what) {
//                case Constants.MESSAGE_STATE_CHANGE:
//                    switch (msg.arg1) {
//                        case BluetoothService.STATE_CONNECTED:
//                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
//                            mConversationArrayAdapter.clear();
//                            break;
//                        case BluetoothService.STATE_CONNECTING:
//                            setStatus(R.string.title_connecting);
//                            break;
//                        case BluetoothService.STATE_LISTEN:
//                        case BluetoothChatService.STATE_NONE:
//                            setStatus(R.string.title_not_connected);
//                            break;
//                    }
//                    break;
//                case Constants.MESSAGE_TOAST:
//                    if (null != activity) {
//                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//            }
        }
    };

    //czy potrzebne?
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        Logger.i("onActivityResult", Integer.toString(requestCode));
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == getActivity().RESULT_OK) {
//                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == getActivity().RESULT_OK) {
//                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == getActivity().RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupServices();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Logger.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), "bt_not_enabled_leaving",
                            Toast.LENGTH_SHORT).show();
                    //this.finish();
                }
        }
    }
}
