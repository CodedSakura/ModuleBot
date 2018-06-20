package modulebot.random;

import modulebot.main.hosts.Command;
import net.dv8tion.jda.core.entities.Message;

import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;

public class Random extends Command {
    @Override
    public String getName() {
        return "random";
    }

    @Override
    public String getHelp() {
        return "Generates random numbers, min inclusive, max exclusive";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"", "{min=1} [max=10] {count=1}"};
    }

    @Override
    public void run(Message m) {
        int min = 1, max = 10, count = 1;
        String[] args = getArgs(m);
        if (args.length == 1) max = Integer.parseInt(args[0]);
        else if (args.length > 1) {
            min = Integer.parseInt(args[0]);
            max = Integer.parseInt(args[1]);
        }
        if (args.length > 2) count = Integer.parseInt(args[2]);
        if (count < 1) {
            send("Can't roll 0 or less dice");
            return;
        }
        StringJoiner sj = new StringJoiner(", ");
        for (int i = 0; i < count; i++) sj.add(Integer.toString(ThreadLocalRandom.current().nextInt(min, max)));
        send("Your random number" + (s(count) ? " is" : "s are") + ":\n" + sj.toString());
    }

    private boolean s(int num) {
        return (num % 10 == 1 && num % 100 != 11);
    }
}
