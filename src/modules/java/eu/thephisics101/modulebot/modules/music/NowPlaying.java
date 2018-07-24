package eu.thephisics101.modulebot.modules.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import eu.thephisics101.modulebot.hosts.Command;
import eu.thephisics101.modulebot.modules.music.classes.GuildMusicManager;
import net.dv8tion.jda.core.entities.Message;

public class NowPlaying extends Command {
    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public String getHelp() {
        return "Returns currently playing track";
    }

    @Override
    public String[] getUsages() {
        return new String[]{""};
    }

    @Override
    public void run(Message m) {
        GuildMusicManager mng = Main.getMusicManager(m.getGuild());
        AudioPlayer player = mng.player;
        AudioTrack track = player.getPlayingTrack();
        if (track != null) {
            send(String.format("Playing: %s\n%s / %s", track.getInfo().title, getTimestamp(track.getPosition()),
                    getTimestamp(track.getDuration())));
        } else send("Nothing is playing");
    }

    private static String getTimestamp(long milliseconds) {
        int seconds = (int)  (milliseconds /  1000          )  % 60 ;
        int minutes = (int) ((milliseconds / (1000 * 60     )) % 60);
        int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }
}
