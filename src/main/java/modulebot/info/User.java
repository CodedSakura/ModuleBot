package modulebot.info;

import modulebot.main.Command;
import net.dv8tion.jda.core.entities.Message;

public class User extends Command {
    @Override
    public String getName() {
        return "user";
    }

    @Override
    public String getHelp() {
        return "Gives information about the user";
    }

    @Override
    public String[] getUsages() {
        return new String[]{""};
    }

    @Override
    public void run(Message m) {

    }
}
