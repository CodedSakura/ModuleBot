package modulebot.calc;

import modulebot.calc.parser.Parser;
import modulebot.main.hosts.Command;
import modulebot.main.hosts.CommandHost;

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
