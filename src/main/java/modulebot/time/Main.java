package modulebot.time;

import modulebot.main.hosts.CH;
import modulebot.main.hosts.Command;

public class Main implements CH {
    @Override
    public Command[] getCommands() {
        return new Command[] { new Time() };
    }

    @Override
    public String getName() {
        return "time";
    }

    @Override
    public String getDescription() {
        return "Gets time from provided timezones";
    }
}
