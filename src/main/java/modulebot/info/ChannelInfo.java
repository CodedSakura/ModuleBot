package modulebot.info;

import modulebot.main.Command;
import net.dv8tion.jda.core.entities.Message;

public class ChannelInfo extends Command {
    @Override
    public String getName() {
        return "channel";
    }

    @Override
    public String getHelp() {
        return "Gives information about a channel";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"[ID]", "[channel name]"};
    }

    @Override
    public void run(Message m) {

    }
}
