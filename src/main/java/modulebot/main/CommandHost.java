package modulebot.main;

public interface CommandHost {
    Command[] getCommands();
    String getName();
    String getDescription();
}
