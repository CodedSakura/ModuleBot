package modulebot.main.cmds;

import modulebot.main.Command;
import net.dv8tion.jda.core.entities.Message;

public class Moudule extends Command {
    @Override
    public String getName() {
        return "module";
    }

    @Override
    public String getHelp() {
        return "Enables, disables and gives info on modules";
    }

    @Override
    public String[] getUsages() {
        return new String[] {"enable [name]", "disable [name]", "info [name]"};
    }

    @Override
    public void run(Message m) throws Exception {
        //TODO: maek dis
    }
}
