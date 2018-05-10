package modulebot.info;

import modulebot.main.Command;
import net.dv8tion.jda.core.entities.Message;

public class RoleInfo extends Command {
    @Override
    public String getName() {
        return "role";
    }

    @Override
    public String getHelp() {
        return "Gives information about a role";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"[ID]", "[role name]"};
    }

    @Override
    public void run(Message m) {

    }
}
