package eu.thephisics101.modulebot.modules.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import eu.thephisics101.modulebot.hosts.Command;
import eu.thephisics101.modulebot.modules.music.classes.GuildMusicManager;
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
