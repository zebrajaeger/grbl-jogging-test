package grbljoggingtest.zebrajaeger.de.grbljoggingtest;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import grbljoggingtest.zebrajaeger.de.grbljoggingtest.command.Commands;
import grbljoggingtest.zebrajaeger.de.grbljoggingtest.grbl.GrblEx;

public class MainActivity extends AppCompatActivity implements BT.ConnectionListener {

    private GestureDetectorCompat mDetector;
    private MyGestureListener myGestureListener;
    private GrblEx grbl;
    private TextView connectionStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        connectionStatusText = (TextView) findViewById(R.id.connection_status);
        setConnectionState(null);
        setSupportActionBar(toolbar);

        BT.I.add(this);
        BT.I.init(this);
        btAutoconnect();

        myGestureListener = new MyGestureListener();
        mDetector = new GestureDetectorCompat(this, myGestureListener);
        mDetector.setIsLongpressEnabled(false);
        mDetector.setOnDoubleTapListener(myGestureListener);

        grbl = new GrblEx(2000);
        new GrblMoveableThread(grbl, myGestureListener).start();
    }

    @Override
    public void onBtConnectionStateChanged(BT.ConnectionState from, BT.ConnectionState to) {
        if (to == BT.ConnectionState.CONNECTED) {
            setConnectionState(BT.I.getCurrentDeviceName());
            grbl.start(BT.I);
            try {
                Thread.sleep(2000);
                grbl.execute(Commands.getInitCommands());
            } catch (InterruptedException e) {
                Log.e("MainActivity", "unable to send initial", e);
            }

        } else if (to == BT.ConnectionState.DISCONNECTED || to == BT.ConnectionState.FAILED) {
            try {
                setConnectionState(null);
                grbl.stop();
            } catch (InterruptedException e) {
                Log.e("MainActivity", "unable to stop grbl", e);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDetector != null) {
            this.mDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    public boolean btAutoconnect() {
        AppData appData = Storage.I.getAppData(getApplicationContext());
        BT.I.refreshDeviceList();
        String btAdapter = appData.getBtAdapter();
        if (BT.I.getSortedDeviceNames().contains(btAdapter)) {
            if (BT.I.connectTo(btAdapter)) {
                return true;
            }
        }
        return false;
    }

    private void setConnectionState(String deviceName){
        if(StringUtils.isNotBlank(deviceName)){
            connectionStatusText.setText("not connected");
        }else{
            connectionStatusText.setText("connected to " + deviceName);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            String btAdapter = Storage.I.getAppData().getBtAdapter();
            BT.I.showChooseDialog(this, btAdapter, new BT.AdapterSelectionResult() {
                @Override
                public void onBTAdapterSelected(String name) {
                    Storage.I.getAppData(getApplicationContext()).setBtAdapter(name);
                    Storage.I.save(getApplicationContext());
                    BT.I.connectTo(name);
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
