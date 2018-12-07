package eu.thephisics101.modulebot.modules.convert;

import eu.thephisics101.modulebot.hosts.CH;
import eu.thephisics101.modulebot.hosts.Command;

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
