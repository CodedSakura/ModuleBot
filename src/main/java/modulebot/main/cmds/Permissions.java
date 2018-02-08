package modulebot.main.cmds;

import modulebot.main.Command;
import net.dv8tion.jda.core.entities.Message;

public class Permissions extends Command {
    @Override
    public String getName() {
        return "perms";
    }

    @Override
    public String getHelp() {
        return "Modifies permission settings";
    }

    @Override
    public String[] getUsages() {
        return new String[] {"perms list", "perms add [id] {'ban'|'admin'}", "perms rm [id]"};
    }

    @Override
    public void run(Message m) throws Exception {
        //TODO: maek dis
    }
}
