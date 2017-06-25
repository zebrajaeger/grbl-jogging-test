package de.zebrajaeger.grbl.jogging.command;

public class Commands {
    private static final String[] CMD_SETUP = {
            "$0=5",
            "$1=25",
            "$2=0",
            "$3=0",
            "$4=1",
            "$5=0",
            "$6=0",
            "$10=1",
            "$11=0.010",
            "$12=0.002",
            "$13=0",
            "$20=0",
            "$21=0",
            "$22=1",
            "$23=0",
            "$24=32.000",
            "$25=32.000",
            "$26=32",
            "$27=1.000",
            "$30=1000",
            "$31=0",
            "$32=0",
            "$100=32.000",
            "$101=32.000",
            "$102=32.000",
            "$110=60000.000",
            "$111=60000.000",
            "$112=60000.000",
            "$120=2000.000",
            "$121=2000.000",
            "$122=2000.000",
            "$130=200.000",
            "$131=200.000",
            "$132=200.000"};

    private static final String[] CMD_INIT = {
            "$X"
    };

    public static CommandList getSetupCommands(){
        return new CommandList(CMD_SETUP);
    }

    public static CommandList getInitCommands(){
        return new CommandList(CMD_INIT);
    }

    public static CommandList getJogCancelCommands(){
        return new CommandList(new String[]{"!~"});
    }
}
