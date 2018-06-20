package modulebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import modulebot.main.hosts.Command;
import modulebot.music.classes.GuildMusicManager;
import modulebot.music.classes.TrackScheduler;
import net.dv8tion.jda.core.entities.Message;

public class Stop extends Command {
    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getHelp() {
        return "Stops the music";
    }

    @Override
    public String[] getUsages() {
        return new String[]{""};
    }

    @Override
    public void run(Message m) {
        GuildMusicManager mng = Main.getMusicManager(m.getGuild());
        AudioPlayer player = mng.player;
        TrackScheduler scheduler = mng.scheduler;
        scheduler.queue.clear();
        player.stopTrack();
        player.setPaused(false);
        send("Playback stopped, queue cleared");
    }
}
