package modulebot.info;

import modulebot.main.hosts.Command;
import net.dv8tion.jda.core.entities.Message;

public class Info extends Command {
    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getHelp() {
        return "Returns info on everything, for specific things, use subcommands";
    }

    @Override
    public String[] getUsages() {
        return new String[] {"[id]", "[user]", "[role]", "[channel]", "[emote]"};
    }

    @Override
    public void run(Message m) {
        String[] args = getArgs(m);
        String ui = UserInfo.get(args, m);
        if (!ui.startsWith("$ERROR$")) { send(ui); return; }
        String ri = RoleInfo.get(args, m);
        if (!ri.startsWith("$ERROR$")) { send(ri); return; }
        String ci = ChannelInfo.get(args, m);
        if (!ci.startsWith("$ERROR$")) { send(ci); return; }
        String ei = EmoteInfo.get(args, m);
        if (!ei.startsWith("$ERROR$")) { send(ei); return; }
        send("ERROR, do specific command for more details");
    }

    static String spaces(String k) {
        StringBuilder sb = new StringBuilder();
        for (int i = k.length(); i < 16; i++) sb.append(" ");
        return sb.toString();
    }
}
