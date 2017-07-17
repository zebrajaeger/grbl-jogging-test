package grbljoggingtest.zebrajaeger.de.grbljoggingtest;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Lars Brandt on 18.06.2017.
 */
public class BT implements Streamable{
    public static final String LOG_TAG = "BT";
    public static final BT I = new BT();
    private static final UUID SerialPortServiceClass_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean isInitialized = false;
    private BluetoothAdapter bta;
    private BluetoothSocket socket = null;
    private BluetoothDevice currentDevice = null;
    private Map<String, BluetoothDevice> devices = Collections.EMPTY_MAP;
    private ConnectionState connectionState = ConnectionState.IDLE;
    private List<ConnectionListener> listeners = new LinkedList<>();

    private BT() {
    }

    public boolean add(ConnectionListener listener) {
        return listeners.add(listener);
    }

    public boolean remove(ConnectionListener listener) {
        return listeners.remove(listener);
    }

    public void init(Context ctx) {
        bta = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    Log.i("BTState", "ACTION_ACL_CONNECTED " + device.getName());

                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    Log.i("BTState", "ACTION_ACL_DISCONNECTED " + device.getName());
                    if (currentDevice == null && currentDevice.getAddress().equals(device.getAddress())) {
                        currentDevice = null;
                    }
                }
            }
        };

        ctx.registerReceiver(broadcastReceiver, filter);
    }

    public boolean connectTo(String name) {
        checkInitialization();
        BluetoothDevice dev = devices.get(name);
        return connectTo(dev);
    }

    public boolean connectTo(BluetoothDevice currentDevice) {
        checkInitialization();
        disconnect();

        this.currentDevice = currentDevice;
        if (this.currentDevice != null) {
            try {
                setConnectionState(ConnectionState.CONNECTING);
                socket = this.currentDevice.createRfcommSocketToServiceRecord(SerialPortServiceClass_UUID);
                socket.connect();
                setConnectionState(ConnectionState.CONNECTED);
            } catch (Exception e) {
                socket = null;
                this.currentDevice = null;
                setConnectionState(ConnectionState.FAILED);
                Log.e(LOG_TAG, "Could not create and connect socket: " + e, e);
            }
        }

        return currentDevice != null;
    }

    public void disconnect() {
        checkInitialization();
        if (this.socket != null) {
            if (this.socket.isConnected()) {
                try {
                    this.socket.close();
                } catch (IOException e) {
                    Log.e("BT", "Could not close socket: " + e, e);
                }
            }
            this.currentDevice = null;
            this.socket = null;
        }
        setConnectionState(ConnectionState.DISCONNECTED);
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    private void setConnectionState(ConnectionState state) {
        // TODO notify Listeners
        ConnectionState prev = connectionState;
        connectionState = state;
        for(ConnectionListener l : listeners){
            l.onBtConnectionStateChanged(prev, state);
        }
    }

    private void checkInitialization() {
        if (!isInitialized) {
            throw new IllegalStateException("not initialized");
        }
    }

    private String createUniqueName(BluetoothDevice d) {
        return d.getName() + "(" + d.getAddress() + ")";
    }

    public void refreshDeviceList() {
        checkInitialization();
        devices = new HashMap<>();
        if (bta != null) {
            for (BluetoothDevice d : bta.getBondedDevices()) {
                devices.put(createUniqueName(d), d);
            }
        }
    }

    public ArrayList<String> getSortedDeviceNames() {
        checkInitialization();
        return new ArrayList(new TreeSet(devices.keySet()));
    }

    public String[] getSortedDeviceNamesAsArray() {
        checkInitialization();
        Set<String> names = devices.keySet();
        return new TreeSet<>(names).toArray(new String[names.size()]);
    }

    public InputStream getInputStream() throws IOException {
        checkInitialization();
        return isConnected() ? socket.getInputStream() : null;
    }

    public OutputStream getOutputStream() throws IOException {
        checkInitialization();
        return isConnected() ? socket.getOutputStream() : null;
    }

    public boolean isConnected() {
        checkInitialization();
        return currentDevice != null && socket != null && socket.isConnected();
    }

    public void showChooseDialog(final Context ctx, String currentAdapteName, final AdapterSelectionResult selectionResult){
        checkInitialization();
        BT.I.refreshDeviceList();
        final String[] names = BT.I.getSortedDeviceNamesAsArray();

        boolean hasDevices = (names.length > 0);
        final AtomicInteger selected = new AtomicInteger(hasDevices ? 0 : -1);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(hasDevices ? "Select Bluetooth Device" : "No Devices Found");
        if (hasDevices) {

            // find index of already selected device if exists
            int checked = 0;
            if (StringUtils.isNotBlank(currentAdapteName)) {
                int pos = 0;
                for (String n : names) {
                    if (currentAdapteName.equals(n)) {
                        checked = pos;
                    }
                    ++pos;
                }
            }

            // title
            builder.setTitle("Select Bluetooth Device");

            // devices
            builder.setSingleChoiceItems(names, checked, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selected.set(which);
                }
            });

            // OK button
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = names[selected.get()];
                    selectionResult.onBTAdapterSelected(name);
                }
            });

            // CANCEL button
            builder.setNegativeButton("Cancel", null);
        } else {

            // no devices found
            builder.setTitle("No Devices Found");
            builder.setPositiveButton("Ok", null);
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public String getCurrentDeviceName() {
        checkInitialization();
        return (currentDevice!=null) ? createUniqueName(currentDevice) : null;
    }

    public enum ConnectionState {
        IDLE, CONNECTING, CONNECTED, FAILED, DISCONNECTED
    }

    public interface ConnectionListener {
        void onBtConnectionStateChanged(ConnectionState from, ConnectionState to);
    }

    public interface AdapterSelectionResult {
        void onBTAdapterSelected(String name);
    }
}
