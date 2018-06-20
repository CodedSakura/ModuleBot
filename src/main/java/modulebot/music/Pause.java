package modulebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import modulebot.main.hosts.Command;
import modulebot.music.classes.GuildMusicManager;
import net.dv8tion.jda.core.entities.Message;

public class Pause extends Command {
    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String getHelp() {
        return "Pauses/resumes music playback";
    }

    @Override
    public String[] getUsages() {
        return new String[]{""};
    }

    @Override
    public void run(Message m) {
        GuildMusicManager mng = Main.getMusicManager(m.getGuild());
        AudioPlayer player = mng.player;
        if (player.getPlayingTrack() == null) {
            send("Queue is empty");
            return;
        }
        player.setPaused(player.isPaused());
        send("Playback " + (player.isPaused() ? "paused" : "resumed"));
    }
}
