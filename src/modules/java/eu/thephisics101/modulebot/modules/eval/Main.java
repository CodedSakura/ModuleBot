package eu.thephisics101.modulebot.modules.eval;

import eu.thephisics101.modulebot.hosts.CH;
import eu.thephisics101.modulebot.hosts.Command;

public class Main implements CH {
    @Override
    public Command[] getCommands() {
        return new Command[] { new Eval() };
    }

    @Override
    public String getName() {
        return "eval";
    }

    @Override
    public String getDescription() {
        return "Evaluates code";
    }
}
