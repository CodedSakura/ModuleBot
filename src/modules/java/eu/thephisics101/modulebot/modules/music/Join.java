package eu.thephisics101.modulebot.modules.music;

import eu.thephisics101.modulebot.hosts.Command;
import eu.thephisics101.modulebot.modules.music.classes.GuildMusicManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.PermissionException;

public class Join extends Command {
    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getHelp() {
        return "Joins a voice channel";
    }

    @Override
    public String[] getUsages() {
        return new String[]{""};
    }

    @Override
    public void run(Message m) {
        String arg = getArg(m);
        VoiceChannel vc = null;
        GuildMusicManager mng = Main.getMusicManager(m.getGuild());
        if (arg.equals("")) {
            if (m.getMember().getVoiceState().inVoiceChannel())
                vc = m.getMember().getVoiceState().getChannel();
        } else {
            if (arg.matches("\\d{17,19}"))
                vc = m.getGuild().getVoiceChannelById(arg);
            if (vc == null)
                vc = m.getGuild().getVoiceChannelsByName(arg, true).get(0);
        }
        if (vc == null) {
            send("No channel found to join");
        } else {
            m.getGuild().getAudioManager().setSendingHandler(mng.sendHandler);
            try {
                m.getGuild().getAudioManager().openAudioConnection(vc);
            } catch (PermissionException e) {
                if (e.getPermission() == Permission.VOICE_CONNECT)
                    send("No permission to join");
            }
        }
    }
}
