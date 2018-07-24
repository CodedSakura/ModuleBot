package eu.thephisics101.modulebot.modules.random;

import eu.thephisics101.modulebot.hosts.Command;
import net.dv8tion.jda.core.entities.Message;

import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;

public class Dice extends Command {
    @Override
    public String getName() {
        return "Dice";
    }

    @Override
    public String getHelp() {
        return "Rolls dice (even impossible ones)";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"{sides=6} {count=1}"};
    }

    @Override
    public void run(Message m) {
        int sides = 6, count = 1;
        String[] args = getArgs(m);
        if (args.length > 1) sides = Integer.parseInt(args[0]);
        if (args.length > 2) count = Integer.parseInt(args[1]);
        if (count < 1) {
            send("Can't roll 0 or less dice");
            return;
        }
        StringJoiner sj = new StringJoiner(", ");
        for (int i = 0; i < count; i++) sj.add(Integer.toString(ThreadLocalRandom.current().nextInt(1, sides + 1)));
        send("Result" + s(count) + ":\n" + sj.toString());
    }

    private String s(int num) {
        return (num % 10 == 1 && num % 100 != 11) ? "" : "s";
    }
}
