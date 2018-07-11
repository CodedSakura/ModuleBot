package modulebot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import modulebot.main.hosts.Command;
import modulebot.music.classes.GuildMusicManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.io.File;

public class Sample extends Command {
    @Override
    public String getName() {
        return "sample";
    }

    @Override
    public String getHelp() {
        return "Plays a sample";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"list", "[name]"};
    }

    @Override
    public void run(Message m) {
        String arg = getArg(m);
        File sampleDir = new File("samples");
        String[] fileNames = sampleDir.list();
        File[] files = sampleDir.listFiles((dir, name) -> name.substring(0, name.lastIndexOf(".")).equals(arg));
        Guild g = m.getGuild();
        if (arg.equals("")) defaultRun();
        else if (fileNames != null) {
            if (arg.equals("list")) {
                StringBuilder sb = new StringBuilder("```\n");
                for (String f : fileNames) sb.append(f, 0, f.lastIndexOf(".")).append("  ");
                send(sb.append("```").toString());
            } else if (files == null || files.length == 0 || files.length > 1) {
                if (files == null || files.length == 0) send("Sample not found");
                else send("Multiple samples with the same name found");
            } else if (m.getMember().getVoiceState().inVoiceChannel()) {
                VoiceChannel vc = m.getMember().getVoiceState().getChannel();
                GuildMusicManager mng = Main.getMusicManager(g);
                if (!g.getAudioManager().isConnected() || g.getAudioManager().getConnectedChannel() != vc) {
                    g.getAudioManager().setSendingHandler(mng.sendHandler);
                    try {
                        g.getAudioManager().openAudioConnection(vc);
                    } catch (PermissionException e) {
                        if (e.getPermission() == Permission.VOICE_CONNECT)
                            send("No permission to join");
                    }
                }
                play(mng, files[0].getAbsolutePath());
            } else {
                send("Join a channel to play a sample in");
            }
        } else send("No samples");
    }


    private void play(GuildMusicManager mng, String url) {
        Main.playerManager.loadItemOrdered(mng, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) { mng.scheduler.queue(track); }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) { }

            @Override
            public void noMatches() { send("Sample not found"); }

            @Override
            public void loadFailed(FriendlyException exception) { send("Could not play: " + exception.getMessage()); }
        });
    }
}
