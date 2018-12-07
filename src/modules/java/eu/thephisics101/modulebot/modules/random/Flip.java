package eu.thephisics101.modulebot.modules.random;

import eu.thephisics101.modulebot.hosts.Command;
import net.dv8tion.jda.core.entities.Message;

import java.util.Random;
import java.util.StringJoiner;

public class Flip extends Command {
    @Override
    public String getName() {
        return "Flip";
    }

    @Override
    public String getHelp() {
        return "Flips coins";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"{count=1}"};
    }

    @Override
    public void run(Message m) {
        int count = 1;
        String[] args = getArgs(m);
        if (args.length > 0) count = Integer.parseInt(args[0]);
        if (count < 1) {
            send("Can't roll 0 or less dice");
            return;
        }
        Random r = new Random();
        StringBuilder sb = new StringBuilder("Result").append(s(count)).append(":\n");
        if (count <= 20) {
            StringJoiner sj = new StringJoiner(", ");
            for (int i = 0; i < count; i++) sj.add(r.nextBoolean() ? "heads" : "tails");
            sb.append(sj.toString());
        } else {
            for (int i = 0; i < count; i++) sb.append(r.nextBoolean() ? "h" : "t");
        }
        send(sb.toString());
    }

    private String s(int num) {
        return (num % 10 == 1 && num % 100 != 11) ? "" : "s";
    }
}
