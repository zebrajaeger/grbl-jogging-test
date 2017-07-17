package grbljoggingtest.zebrajaeger.de.grbljoggingtest;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import grbljoggingtest.zebrajaeger.de.grbljoggingtest.command.Commands;
import grbljoggingtest.zebrajaeger.de.grbljoggingtest.grbl.GrblEx;
import grbljoggingtest.zebrajaeger.de.grbljoggingtest.moveable.Move;
import grbljoggingtest.zebrajaeger.de.grbljoggingtest.moveable.Moveable;

/**
 * Created by Lars Brandt on 16.07.2017.
 */
public class GrblMoveableThread extends Thread {

    private final GrblEx grbl;
    private final Moveable moveable;
    private Context ctx;

    public GrblMoveableThread(Context ctx, GrblEx grbl, Moveable moveable) {
        super("GrblMoveableThread");
        this.ctx = ctx;
        this.grbl = grbl;
        this.moveable = moveable;
    }

    @Override
    public void run() {
        long nextPosCall = 0;
        long readPosPeriod = 500;

        try {
            while (true) {
                long now = System.currentTimeMillis();
                if (nextPosCall <= now) {
                    nextPosCall = now + readPosPeriod;
                    StatusReportResponse statusReportResponse = StatusReportResponse.of(grbl.execute("?"));
                    Intent intent = new Intent();
                    intent.setAction("de.zebrajaeger.grbl.broadcast.STATUS_REPORT");
                    intent.putExtra("data", statusReportResponse);
                    ctx.sendBroadcast(intent);
                }

                Move move = moveable.pickMove();
                if (move != null && move.actionRequired()) {

                    if (move.isRequireStopX()) {
                        Log.i("GrblLoop", "STOP");
                        grbl.execute(Commands.getJogCancelCommands());
                    }
                    float diff = move.getDeltaX();
                    if (diff != 0) {
                        diff /= 10.0f;
                        grbl.execute("$J=G91 F10000 G20 X" + diff + "\n");
                    }
                } else {
                    // the polling way is easy to implement and this is just a test...
                    // better: use notify() and wait() so the thread does'nt has to run 100 times per second
                    Thread.sleep(10);
                }
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}
