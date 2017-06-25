This is only for testing the jogging function of grbl 1.1f and newer.

It uses only the x-axis!

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

