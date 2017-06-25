package de.zebrajaeger.grbl.jogging;

import com.fazecast.jSerialComm.SerialPort;
import de.zebrajaeger.grbl.jogging.command.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class App extends JFrame {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        new App().go();
    }

    private GrblEx grbl;

    private void go() {
        String portName = "COM6";

        SerialPort port = null;
        for (SerialPort p : SerialPort.getCommPorts()) {
            if (p.getSystemPortName().equals(portName)) {
                port = p;
                LOG.info("** Port available: '{}' : '{}'", p.getSystemPortName(), p.getDescriptivePortName());
            } else {
                LOG.info("Port available: '{}' : '{}'", p.getSystemPortName(), p.getDescriptivePortName());
            }
        }

        if (port == null) {
            LOG.error("Could not find port with system name: '{}", portName);
        } else {
            port.setBaudRate(115200);
            port.openPort();
            runApp(port);
        }
    }

    private void runApp(SerialPort port) {
        grbl = new GrblEx(port, 2000);

        setSize(400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        //setAlwaysOnTop(true);

        MyListener l = new MyListener();
        addMouseMotionListener(l);
        addMouseListener(l);

        new Thread() {
            @Override
            public void run() {
                for (; ; ) {
                    Move move = l.pickMove();
                    try {
                        if (move.actionRequired()) {

                            if (move.isRequireStopX()) {
                                LOG.info("STOP");
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
            LOG.error("unable to send initial", e);
        }
    }
}
