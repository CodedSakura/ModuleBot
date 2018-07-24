package eu.thephisics101.modulebot.modules.music;

import eu.thephisics101.modulebot.hosts.Command;
import net.dv8tion.jda.core.entities.Message;

public class Skip extends Command {
    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getHelp() {
        return "Skips currently playing track";
    }

    @Override
    public String[] getUsages() {
        return new String[]{""};
    }

    @Override
    public void run(Message m) {
        Main.getMusicManager(m.getGuild()).scheduler.nextTrack();
        send("Skipped to next track");
    }
}
