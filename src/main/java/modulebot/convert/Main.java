package modulebot.convert;

import modulebot.main.hosts.CH;
import modulebot.main.hosts.Command;

public class Main implements CH {
    @Override
    public Command[] getCommands() {
        return new Command[] { new Convert() };
    }

    @Override
    public String getName() {
        return "convert";
    }

    @Override
    public String getDescription() {
        return "Converts units";
    }
}
