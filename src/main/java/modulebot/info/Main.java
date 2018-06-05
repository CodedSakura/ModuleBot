package modulebot.info;

import modulebot.main.hosts.Command;
import modulebot.main.hosts.CommandHost;

public class Main extends CommandHost {
    @Override
    public Command[] getCommands() {
        return new Command[] {
                new Info(),
                new UserInfo(),
                new RoleInfo(),
                new ChannelInfo(),
                new EmoteInfo()
        };
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Gives info on roles, users, emotes, etc.";
    }
}
