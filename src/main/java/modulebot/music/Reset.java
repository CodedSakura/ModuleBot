package modulebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import modulebot.main.hosts.Command;
import modulebot.music.classes.GuildMusicManager;
import modulebot.music.classes.TrackScheduler;
import net.dv8tion.jda.core.entities.Message;

public class Reset extends Command {
    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getHelp() {
        return "Resets the music player";
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
        synchronized (Main.musicManagers) {
            scheduler.queue.clear();
            player.destroy();
            m.getGuild().getAudioManager().setSendingHandler(null);
            Main.musicManagers.remove(m.getGuild().getIdLong());
        }
        mng = Main.getMusicManager(m.getGuild());
        m.getGuild().getAudioManager().setSendingHandler(mng.sendHandler);
        send("Player completely reset");
    }
}
