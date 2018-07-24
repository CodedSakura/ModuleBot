package eu.thephisics101.modulebot.modules.time;

import eu.thephisics101.modulebot.hosts.CH;
import eu.thephisics101.modulebot.hosts.Command;

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
