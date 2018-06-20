package modulebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import modulebot.main.hosts.Command;
import net.dv8tion.jda.core.entities.Message;

public class Volume extends Command {
    @Override
    public String getName() {
        return "volume";
    }

    @Override
    public String getHelp() {
        return "Adjusts player's volume";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"", "[val (0-100)]"};
    }

    @Override
    public void run(Message m) {
        AudioPlayer player = Main.getMusicManager(m.getGuild()).player;
        String arg = getArg(m);
        if (arg.equals("")) send("Current volume: " + player.getVolume());
        else {
            try {
                player.setVolume(Math.max(0, Math.min(100, Integer.parseInt(arg))));
                send("Volume set to " + player.getVolume());
            } catch (NumberFormatException e) {
                send("Not a valid integer");
            }
        }
    }
}
