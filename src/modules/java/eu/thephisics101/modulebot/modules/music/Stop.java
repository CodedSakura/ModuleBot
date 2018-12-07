package eu.thephisics101.modulebot.modules.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import eu.thephisics101.modulebot.hosts.Command;
import eu.thephisics101.modulebot.modules.music.classes.GuildMusicManager;
import eu.thephisics101.modulebot.modules.music.classes.TrackScheduler;
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
