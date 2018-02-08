package modulebot.info;

import modulebot.main.Command;
import net.dv8tion.jda.core.entities.Message;

public class Info extends Command {
    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getHelp() {
        return "Returns info on everything, for specific things, use subcommands";
    }

    @Override
    public String[] getUsages() {
        return new String[] {"[id]", "[user]", "[role]", "[channel]", "[emote]"};
    }

    @Override
    public void run(Message m) {
        String[] args = getArgs(m);
        send(args[0]);
    }
}
