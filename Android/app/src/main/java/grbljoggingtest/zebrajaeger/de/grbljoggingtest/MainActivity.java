package grbljoggingtest.zebrajaeger.de.grbljoggingtest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

import grbljoggingtest.zebrajaeger.de.grbljoggingtest.command.Commands;
import grbljoggingtest.zebrajaeger.de.grbljoggingtest.grbl.GrblEx;
import grbljoggingtest.zebrajaeger.de.grbljoggingtest.moveable.Move;
import grbljoggingtest.zebrajaeger.de.grbljoggingtest.moveable.Moveable;

public class MainActivity extends AppCompatActivity {

    private GestureDetectorCompat mDetector;
    private MyGestureListener myGestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btAutoconnect();
        myGestureListener = new MyGestureListener();
        mDetector = new GestureDetectorCompat(this, myGestureListener);
        mDetector.setIsLongpressEnabled(false);
        mDetector.setOnDoubleTapListener(myGestureListener);

        GrblEx grbl = new GrblEx(BT.I, 2000);
        initGrbl(grbl, myGestureListener);
    }

    private void initGrbl(final GrblEx grbl, final Moveable moveable) {

        new Thread(){
            @Override
            public void run() {
                for (; ; ) {
                    Move move = moveable.pickMove();
                    try {
                        if (move != null && move.actionRequired()) {

                            if (move.isRequireStopX()) {
                                Log.i("GrblLoop", "STOP");
                                grbl.execute(Commands.getJogCancelCommands());
                            }
                            float diff = move.getDeltaX();
                            if (diff != 0) {
                                diff /= 10.0f;
                                grbl.execute("$J=G91 F10000 G20 X" + diff);
                            }
                        } else {
                            // the polling way is easy to implement and this is just a test...
                            // better: use notify() and wait() so the thread does'nt has to run 100 times per second
                            Thread.sleep(10);
                        }
                    } catch (InterruptedException e) {
                        interrupt();
                    }
                }
            }
        }.start();

        try {
            Thread.sleep(2000);
            grbl.execute(Commands.getInitCommands());
        } catch (InterruptedException e) {
            Log.e("GrblLoop", "unable to send initial", e);
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
            if (BT.I.setCurrentDevice(btAdapter)) {
                return true;
            }
        }
        return false;
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
            buttonBtSelectDeviceOnClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonBtSelectDeviceOnClick() {
        BT.I.refreshDeviceList();
        final String[] names = BT.I.getSortedDeviceNamesAsArray();

        boolean hasDevices = (names.length > 0);
        final AtomicInteger selected = new AtomicInteger(hasDevices ? 0 : -1);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(hasDevices ? "Select Bluetooth Device" : "No Devices Found");
        if (hasDevices) {

            // find index of already selected device if exists
            int checked = 0;
            String btAdapter = Storage.I.getAppData().getBtAdapter();
            if (StringUtils.isNotBlank(btAdapter)) {
                int pos = 0;
                for (String n : names) {
                    if (btAdapter.equals(n)) {
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
                    Storage.I.getAppData(getApplicationContext()).setBtAdapter(name);
                    Storage.I.save(getApplicationContext());
                    BT.I.setCurrentDevice(name);
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
}
