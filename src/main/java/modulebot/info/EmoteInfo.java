package modulebot.info;

import modulebot.main.hosts.Command;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmoteInfo extends Command {
    @Override
    public String getName() {
        return "emote";
    }

    @Override
    public String getHelp() {
        return "Gives information about emotes";
    }

    @Override
    public String[] getUsages() {
        return new String[]{""};
    }

    @Override
    public void run(Message m) {
        String o = get(getArgs(m), m);
        if (o.startsWith("$ERROR$")) o = o.substring(7);
        send(o);
    }

    private static final String[] ERR = {
            "$ERROR$No role specified",
            "$ERROR$Multiple roles found, use ID if possible",
            "$ERROR$Role not found"
    };

    static String get(String[] args, Message m) {
        Emote e;
        if (args.length == 0) return ERR[0];
        else {
            if (args[0].matches("\\d{17,18}") && m.getGuild().getRoleById(args[0]) != null)
                e = m.getGuild().getEmoteById(args[0]);
            else {
                List<Emote> emotes = m.getGuild().getEmotesByName(args[0], true);
                if (m.getEmotes().size() > 0) {
                    if (m.getEmotes().size() > 1) return ERR[1];
                    else e = m.getEmotes().get(0);
                } else if (emotes.size() != 0) {
                    if (emotes.size() > 1) {
                        List<Emote> emotes2 = m.getGuild().getEmotesByName(args[0], false);
                        if (emotes2.size() != 0) {
                            if (emotes2.size() > 1) return ERR[1];
                            else e = emotes2.get(0);
                        } else return ERR[2];
                    } else e = emotes.get(0);
                } else return ERR[2];
            }
        }
        if (e == null) return ERR[2];
        StringBuilder sb = new StringBuilder("```\n");
        Map<String, String> s = new LinkedHashMap<>();
        s.put("name",          e.getName());
        s.put("id",            e.getId());
        s.put("created",       e.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME));
        s.put("animated",      e.isAnimated() ? "yes" : "no");
        s.put("managed",       e.isManaged() ? "yes" : "no");
        for (String k : s.keySet()) sb.append(k).append(Info.spaces(k)).append(s.get(k)).append("\n");
        return sb.append("\n```").append(e.getImageUrl()).toString();
    }
}
