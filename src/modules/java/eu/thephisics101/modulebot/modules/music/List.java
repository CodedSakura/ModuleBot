package eu.thephisics101.modulebot.modules.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import eu.thephisics101.modulebot.hosts.Command;
import eu.thephisics101.modulebot.modules.music.classes.GuildMusicManager;
import eu.thephisics101.modulebot.modules.music.classes.TrackScheduler;
import net.dv8tion.jda.core.entities.Message;

import java.util.Queue;

public class List extends Command {
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getHelp() {
        return "Returns next 10 queue entries";
    }

    @Override
    public String[] getUsages() {
        return new String[]{""};
    }

    @Override
    public void run(Message m) {
        GuildMusicManager mng = Main.getMusicManager(m.getGuild());
        TrackScheduler scheduler = mng.scheduler;
        Queue<AudioTrack> queue = scheduler.queue;
        if (queue.isEmpty()) {
            send("Queue is empty");
        } else {
            int count = 0;
            long length = 0;
            StringBuilder sb = new StringBuilder("Current queue: entries: ").append(queue.size()).append("\n");
            for (AudioTrack track : queue) {
                length += track.getDuration();
                if (count++ < 10) {
                    sb.append("`[").append(getTimestamp(track.getDuration())).append("]` ").append(track.getInfo().title)
                            .append("\n");
                }
            }
            sb.append("\nTotal length: ").append(getTimestamp(length));
            send(sb.toString());
        }
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
