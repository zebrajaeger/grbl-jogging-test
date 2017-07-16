#GrblJoggingTest
This is only for testing the jogging function of gnea/grbl 1.1.

This is only for testing the jogging function of grbl 1.1f and newer.
Requires a Grbl(gnea, v1.1) Arduino with connected X-Axis

My Grbl Settings are:

    $0=5
    $1=255
    $2=0
    $3=0
    $4=1
    $5=0
    $6=0
    $10=1
    $11=0.010
    $12=0.002
    $13=0
    $20=0
    $21=0
    $22=0
    $23=0
    $24=32.000
    $25=32.000
    $26=32
    $27=1.000
    $30=1000
    $31=0
    $32=0
    $100=32.000
    $101=32.000
    $102=32.000
    $110=60000.000
    $111=60000.000
    $112=60000.000
    $120=10000.000
    $121=2000.000
    $122=2000.000
    $130=200.000
    $131=200.000
    $132=200.000


# Java App
When you start the program wait a little moment (~5 seconds) to let grbl initialize, 
otherwise you get some trouble (remeber: this is just a test).
On the empty window, press the left mouse button and move the mouse (only the x-axis is interpreted).

To choose the correct com port, you can start the App (de.zebrajaeger.grbl.jogging.App) 
and take a look at the console output. On Windows it looks like this:

    [main] INFO de.zebrajaeger.grbl.jogging.App - Port available: 'COM1' : 'Kommunikationsanschluss (COM1)'
    [main] INFO de.zebrajaeger.grbl.jogging.App - Port available: 'COM3' : 'USB-SERIAL CH340 (COM3)'
    [main] INFO de.zebrajaeger.grbl.jogging.App - ** Port available: 'COM6' : 'USB-SERIAL CH340 (COM6)'
    [Thread-1] INFO de.zebrajaeger.grbl.jogging.Grbl - ResultListener: Thread started
    [Thread-2] INFO de.zebrajaeger.grbl.jogging.Grbl - CommandSender: thread started
 
Into the class de.zebrajaeger.grbl.jogging.App change the literal in  ~ line 20
 
    String portName = "COM6";

to whatever port your grbl is connected. Then restart.    
In my case i have a Arduino nano clone with a ch340 and a grbl 1.1f flashed.

#AndroidApp

Reqires a paired Bluetooth-Serial-Device (like HC-05) where a Arduino with programmed Grbl is connected;


