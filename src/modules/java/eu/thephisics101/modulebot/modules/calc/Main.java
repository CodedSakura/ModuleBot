package eu.thephisics101.modulebot.modules.calc;

import eu.thephisics101.modulebot.modules.calc.parser.Parser;
import eu.thephisics101.modulebot.hosts.Command;
import eu.thephisics101.modulebot.hosts.CommandHost;

import java.util.HashMap;

public class Main extends CommandHost {
    static HashMap<Long, Parser> parser = new HashMap<>();

    @Override
    public Command[] getCommands() {
        return new Command[] {
                new Calc(),
                new Var()
        };
    }

    @Override
    public String getName() {
        return "calc";
    }

    @Override
    public String getDescription() {
        return "A simple calculation command";
    }
}
