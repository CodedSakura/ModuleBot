package modulebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import modulebot.main.hosts.Command;
import modulebot.music.classes.GuildMusicManager;
import modulebot.music.classes.TrackScheduler;
import net.dv8tion.jda.core.entities.Message;

public class Restart extends Command {
    @Override
    public String getName() {
        return "restart";
    }

    @Override
    public String getHelp() {
        return "Restarts last/current track";
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
        AudioTrack track = player.getPlayingTrack();
        if (track == null) track = scheduler.lastTrack;
        if (track == null) {
            send("No track to restart");
        } else {
            player.playTrack(track);
            send("Restarting track: " + track.getInfo().title);
        }
    }
}
