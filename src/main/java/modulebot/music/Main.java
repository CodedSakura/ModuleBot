package modulebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import modulebot.main.hosts.Command;
import modulebot.main.hosts.CommandHost;
import modulebot.music.classes.GuildMusicManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;

import java.util.HashMap;
import java.util.Map;

public class Main extends CommandHost {
    private static final byte DEFAULT_VOLUME = 50;

    static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    static final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

    @Override
    public Command[] getCommands() {
        return new Command[] {
                new Join(),
                new Leave(),
                new Play(),
                new PPlay(),
                new Skip(),
                new Pause(),
                new Stop(),
                new Volume(),
                new Restart(),
                new Reset(),
                new NowPlaying(), new NP(),
                new List(), new Queue(),
                new Shuffle(),
                new Sample()
        };
    }

    @Override
    public String getName() {
        return "music";
    }

    @Override
    public String getDescription() {
        return "Plays music, either from samples or streams (YouTube, SoundCloud, etc.)";
    }

    @Override
    public void onReady(ReadyEvent event) {
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    static GuildMusicManager getMusicManager(Guild g) {
        long gid = g.getIdLong();
        GuildMusicManager musicManager = musicManagers.get(gid);
        if (musicManager == null) {
            synchronized (musicManagers) {
                musicManager = musicManagers.get(gid);
                if (musicManager == null) {
                    musicManager = new GuildMusicManager(playerManager);
                    musicManager.player.setVolume(DEFAULT_VOLUME);
                    musicManagers.put(gid, musicManager);
                }
            }
        }
        return musicManager;
    }
}
