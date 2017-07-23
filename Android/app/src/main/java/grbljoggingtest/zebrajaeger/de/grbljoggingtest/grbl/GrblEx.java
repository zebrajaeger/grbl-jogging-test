package grbljoggingtest.zebrajaeger.de.grbljoggingtest.grbl;

import android.util.Log;

import grbljoggingtest.zebrajaeger.de.grbljoggingtest.command.Command;
import grbljoggingtest.zebrajaeger.de.grbljoggingtest.command.CommandList;

public class GrblEx extends Grbl implements Grbl.Listener {
    private Object cmdLock = new Object();
    private String lastAnswer;
    private int timeout;

    public GrblEx(int timeout) {
        this.timeout = timeout;
        setListener(this);
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
            if(addCommand(cmd)) {
                cmdLock.wait(timeout);
                return lastAnswer;
            }else{
                return null;
            }
        }
    }

    @Override
    public void onGrblResult(String result) {
        Log.d("GrblEx", "Result received: '" + result + "'");
        synchronized (cmdLock) {
            lastAnswer = result;
            cmdLock.notify();
        }
    }
}
