package modulebot.eval;

import modulebot.main.hosts.CH;
import modulebot.main.hosts.Command;

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
