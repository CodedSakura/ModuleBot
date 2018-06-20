package modulebot.music;

import modulebot.main.hosts.Command;
import net.dv8tion.jda.core.entities.Message;

public class Leave extends Command {
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getHelp() {
        return "Makes the bot leave the voice channel";
    }

    @Override
    public String[] getUsages() {
        return new String[]{""};
    }

    @Override
    public void run(Message m) {
        m.getGuild().getAudioManager().setSendingHandler(null);
        m.getGuild().getAudioManager().closeAudioConnection();
    }
}
