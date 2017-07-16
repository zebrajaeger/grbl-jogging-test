package grbljoggingtest.zebrajaeger.de.grbljoggingtest.command;

public class Commands {
    public static CommandList getInitCommands() {
        return new CommandList(new String[]{"$X"});
    }

    public static CommandList getJogCancelCommands() {
        return new CommandList(new String[]{"!~"});
    }
}
