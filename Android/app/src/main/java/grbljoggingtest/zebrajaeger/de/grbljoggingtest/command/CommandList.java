package grbljoggingtest.zebrajaeger.de.grbljoggingtest.command;

import java.util.LinkedList;

public class CommandList {
    private LinkedList<Command> commands = new LinkedList<>();

    public CommandList(String[] cmds) {
        this.commands = commands;
        for (String cmd : cmds) {
            add(new Command(cmd));
        }
    }

    public boolean add(Command blockingCommand) {
        return commands.add(blockingCommand);
    }

    public boolean isOk() {
        for (Command c : commands) {
            if (!c.isOk()) {
                return false;
            }
        }
        return true;
    }

    public LinkedList<Command> getCommands() {
        return commands;
    }
}
