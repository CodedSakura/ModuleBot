package modulebot.main.cmds;

import modulebot.main.Command;
import net.dv8tion.jda.core.entities.Message;

public class Ping extends Command {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getHelp() {
        return "Returns the ping of the bot";
    }

    @Override
    public String[] getUsages() {
        return new String[] {""};
    }

    @Override
    public void run(Message m) {
        send("Ping: " + m.getJDA().getPing() + " ms");
    }
}
