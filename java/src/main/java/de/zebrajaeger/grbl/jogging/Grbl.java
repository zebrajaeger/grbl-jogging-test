package de.zebrajaeger.grbl.jogging;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class Grbl {

    private static final Logger LOG = LoggerFactory.getLogger(Grbl.class);

    private SerialPort port;

    private ResultListener resultListener;
    private CommandSender commandSender;

    public Grbl(SerialPort port) {
        this.port = port;
    }

    public void addListener(Listener listener) {
        resultListener.addListener(listener);
    }

    public void addCommand(String cmd) {
        commandSender.addCommand(cmd);
    }

    public void start() {
        if ((resultListener != null && resultListener.isAlive())
                || (commandSender != null && commandSender.isAlive())) {
            throw new IllegalStateException("at least one thread is running");
        } else {
            resultListener = new ResultListener(port.getInputStream());
            commandSender = new CommandSender(port.getOutputStream());

            resultListener.start();
            commandSender.start();
        }
    }

    public void stop() throws InterruptedException {
        if (resultListener != null) {
            resultListener.interrupt();
            resultListener.join();
        }

        if (commandSender != null) {
            commandSender.interrupt();
            commandSender.join();
        }
    }

    public interface Listener {
        void onGrblResult(String result);
    }

    private class ResultListener extends Thread {

        private InputStream inputStream;
        private List<Listener> listener = new LinkedList<>();

        public ResultListener(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public void addListener(Listener listener) {
            this.listener.add(listener);
        }

        @Override
        public void run() {
            LOG.info("ResultListener: Thread started");
            StringBuilder sb = new StringBuilder(128);
            try {
                for (int received; (received = inputStream.read()) >= 0; ) {
                    if (received == '\r' || received == '\n') {
                        if (listener != null && sb.length() > 0) {
                            LOG.debug("ResultListener: Result received: '{}'", sb.toString());
                            String result = sb.toString();
                            for (Listener l : listener) {
                                l.onGrblResult(result);
                            }
                        }
                        sb = new StringBuilder(128);
                    } else {
                        sb.append((char) received);
                    }
                }
            } catch (IOException e) {
                LOG.error("ResultListener: Failed reading from stream", e);
            } finally {
                LOG.info("ResultListener: ResultListener: Thread stopped");
            }
        }
    }

    private class CommandSender extends Thread {

        private OutputStream outputStream;
        private LinkedList<String> cmds = new LinkedList<>();

        public void addCommand(String cmd) {
            LOG.debug("CommandSender: add new command: '{}'", cmd);
            synchronized (this) {
                cmds.add(cmd);
                notify();
            }
            LOG.debug("CommandSender: command added: '{}'", cmd);
        }

        public CommandSender(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            LOG.info("CommandSender: thread started");
            try {
                for (; ; ) {
                    synchronized (this) {
                        while (cmds.isEmpty()) {
                            wait();
                        }
                        String toSend = cmds.removeLast();
                        LOG.debug("CommandSender: send new command: '{}'", toSend);
                        outputStream.write((toSend + "\n").getBytes());
                        outputStream.flush();
                        LOG.debug("CommandSender: command written: '{}'", toSend);
                    }
                }
            } catch (InterruptedException e) {
                interrupt();
                LOG.info("CommandSender: Sender interrupted");
            } catch (IOException e) {
                LOG.info("CommandSender: could not send, e");
            } finally {
                LOG.info("CommandSender: thread stopped");
            }
        }
    }
}
