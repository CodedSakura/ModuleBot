package modulebot.music;

import modulebot.main.hosts.Command;
import modulebot.music.classes.GuildMusicManager;
import modulebot.music.classes.TrackScheduler;
import net.dv8tion.jda.core.entities.Message;

public class Shuffle extends Command {
    @Override
    public String getName() {
        return "shuffle";
    }

    @Override
    public String getHelp() {
        return "Shuffles the queue";
    }

    @Override
    public String[] getUsages() {
        return new String[]{""};
    }

    @Override
    public void run(Message m) {
        GuildMusicManager mng = Main.getMusicManager(m.getGuild());
        TrackScheduler scheduler = mng.scheduler;
        if (scheduler.queue.isEmpty()) send("The queue is empty");
        else {
            scheduler.shuffle();
            send("Queue shuffled");
        }
    }
}
