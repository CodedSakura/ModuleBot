package modulebot.info;

import modulebot.main.hosts.Command;
import net.dv8tion.jda.core.entities.Message;

public class ${NAME} extends Command {
    @Override
    public String getName() {
        #set($name = $NAME.toLowerCase())
        return "${name}";
    }

    @Override
    public String getHelp() {
        return "${INFO}";
    }

    @Override
    public String[] getUsages() {
        return new String[] {""};
    }

    @Override
    public void run(Message m) {
        
    }
}
