package modulebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import modulebot.main.hosts.Command;
import modulebot.music.classes.GuildMusicManager;
import net.dv8tion.jda.core.entities.Message;

public class Play extends Command {
    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getHelp() {
        return "Plays music";
    }

    @Override
    public String[] getUsages() {
        //noinspection SpellCheckingInspection
        return new String[]{"{link}", "ytsearch:[search]"};
    }

    @Override
    public void run(Message m) {
        GuildMusicManager mng = Main.getMusicManager(m.getGuild());
        AudioPlayer player = mng.player;
        String arg = getArg(m);
        if (arg.equals("")) {
            if (player.isPaused()) {
                player.setPaused(false);
                send("Playback resumed");
            } else if (player.getPlayingTrack() != null) {
                send("Player already playing");
            } else if (mng.scheduler.queue.isEmpty()) {
                send("Queue is empty");
            } else {
                send("**ERROR**: UnknownException");
            }
        } else play(mng, arg);
    }

    private void play(GuildMusicManager mng, String url) {
        if (url.startsWith("\u202a")) url = url.substring(1);
        if (url.startsWith("<") && url.endsWith(">")) url = url.substring(1, url.length() - 1);

        Main.playerManager.loadItemOrdered(mng, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                String msg = "Added to queue: " + track.getInfo().title;
                if (mng.player.getPlayingTrack() == null) msg += "\nand started playback;";

                mng.scheduler.queue(track);
                send(msg);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                if (firstTrack == null) firstTrack = playlist.getTracks().get(0);

                mng.scheduler.queue(firstTrack);
                send("Adding to queue " + firstTrack.getInfo().title + " (first of playlist " + playlist.getName() + ")");
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
