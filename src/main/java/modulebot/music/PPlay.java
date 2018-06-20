package modulebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import modulebot.main.hosts.Command;
import modulebot.music.classes.GuildMusicManager;
import net.dv8tion.jda.core.entities.Message;

public class PPlay extends Command {
    @Override
    public String getName() {
        return "pplay";
    }

    @Override
    public String getHelp() {
        return "Plays a playlist";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"[url]"};
    }

    @Override
    public void run(Message m) {
        play(Main.getMusicManager(m.getGuild()), getArg(m));
    }

    private void play(GuildMusicManager mng, String url) {
        if (url.startsWith("<") && url.endsWith(">")) url = url.substring(1, url.length() - 1);

        Main.playerManager.loadItemOrdered(mng, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                String msg = "Added to queue: " + track.getInfo().title;
                if (mng.player.getPlayingTrack() == null) msg += "\nAnd the player has started playing;";

                mng.scheduler.queue(track);
                send(msg);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                playlist.getTracks().forEach(mng.scheduler::queue);
                send("Added " + playlist.getTracks().size() +" tracks from playlist: " + playlist.getName());
            }

            @Override
            public void noMatches() {
                send("Nothing found");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                send("Could not play: " + exception.getMessage());
            }
        });
    }
}
