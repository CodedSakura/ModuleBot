package eu.thephisics101.modulebot.modules.random;

import eu.thephisics101.modulebot.hosts.CH;
import eu.thephisics101.modulebot.hosts.Command;

public class Main implements CH {
    @Override
    public Command[] getCommands() {
        return new Command[] {
                new Random(),
                new Dice(),
                new Flip(),
                new EightBall()
        };
    }

    @Override
    public String getName() {
        return "random";
    }

    @Override
    public String getDescription() {
        return "Commands that use random numbers";
    }
}
