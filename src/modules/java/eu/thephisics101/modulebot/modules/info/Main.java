package eu.thephisics101.modulebot.modules.info;

import eu.thephisics101.modulebot.hosts.CH;
import eu.thephisics101.modulebot.hosts.Command;

public class Main implements CH {
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
