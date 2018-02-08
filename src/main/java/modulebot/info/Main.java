package modulebot.info;

import modulebot.main.Command;
import modulebot.main.CommandHost;

public class Main implements CommandHost {
    @Override
    public Command[] getCommands() {
        return new Command[] {
                new Info()
        };
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Gives info on roles, users, emotes, etc.";
    }
}
