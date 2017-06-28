package de.zebrajaeger.grbl.jogging.command;

public class Command {
    private String command;
    private String answer;

    public Command(String command) {
        this.command = command;
    }

    public boolean isOk() {
        return "ok".equals(answer);
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCommand() {
        return command;
    }

    public String getAnswer() {
        return answer;
    }
}
