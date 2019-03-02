package eu.thephisics101.modulebot.modules.music.classes;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {
    public AudioPlayer player;
    public TrackScheduler scheduler;
    public AudioPlayerSendHandler sendHandler;

    public GuildMusicManager(AudioPlayerManager manager) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player);
        sendHandler = new AudioPlayerSendHandler(player);
        player.addListener(scheduler);
    }
}
