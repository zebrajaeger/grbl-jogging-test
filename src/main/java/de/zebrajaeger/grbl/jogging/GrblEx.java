package de.zebrajaeger.grbl.jogging;

import com.fazecast.jSerialComm.SerialPort;
import de.zebrajaeger.grbl.jogging.command.Command;
import de.zebrajaeger.grbl.jogging.command.CommandList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrblEx extends Grbl implements Grbl.Listener {
    private static final Logger LOG = LoggerFactory.getLogger(GrblEx.class);
    private Object cmdLock = new Object();
    private String lastAnswer;

    public GrblEx(SerialPort port) {
        super(port);
        start();
        addListener(this);
    }

    public void execute(CommandList list) throws InterruptedException {
        for (Command c : list.getCommands()) {
            c.setAnswer(execute(c.getCommand()));
        }
    }

    public void execute(Command command) throws InterruptedException {
            command.setAnswer(execute(command.getCommand()));
    }

    public String execute(String cmd) throws InterruptedException {
        synchronized (cmdLock) {
            addCommand(cmd);
            cmdLock.wait();
            return lastAnswer;
        }
    }

    @Override
    public void onGrblResult(String result) {
        LOG.debug("APP: Result received: '{}'", result);
        synchronized (cmdLock) {
            lastAnswer = result;
            cmdLock.notify();
        }
    }
}
