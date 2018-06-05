package modulebot.main.hosts;

import net.dv8tion.jda.core.entities.Message;

interface CMDI {
    String getName();
    String getHelp();
    String[] getUsages();
    void run(Message m) throws Exception;
}
