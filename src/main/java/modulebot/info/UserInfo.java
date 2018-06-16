package modulebot.info;

import modulebot.main.hosts.Command;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserInfo extends Command {
    @Override
    public String getName() {
        return "user";
    }

    @Override
    public String getHelp() {
        return "Gives information about the user";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"[ID]", "[username]", "[nickname]"};
    }

    @Override
    public void run(Message m) {
        String o = get(getArg(m), m);
        if (o.startsWith("$ERROR$")) o = o.substring(7);
        send(o);
    }

    static String get(String args, Message m) {
        User u = null;
        if (args.equals("")) u = m.getAuthor();
        else {
            if (args.matches("\\d{17,18}") && m.getJDA().getUserById(args) != null)
                u = m.getJDA().getUserById(args);
            else {
                int e = 0;
                List<User> users = m.getJDA().getUsersByName(args, true);
                List<Member> members = m.getGuild().getMembersByEffectiveName(args, true);
                if (m.getMentionedUsers().size() > 0) {
                    if (m.getMentionedUsers().size() > 1) e = 1;
                    else u = m.getMentionedUsers().get(0);
                }
                if (users.size() != 0) {
                    if (users.size() > 1) {
                        List<User> users2 = m.getJDA().getUsersByName(args, false);
                        if (users2.size() != 0) {
                            if (users2.size() > 1) e = 1;
                            else u = users2.get(0);
                        } else e = 1;
                    } else u = users.get(0);
                }
                if (members.size() != 0) {
                    if (members.size() > 1) {
                        List<Member> members2 = m.getGuild().getMembersByEffectiveName(args, false);
                        if (members2.size() != 0) {
                            if (members2.size() > 1) e = 1;
                            else u = members2.get(0).getUser();
                        } else e = 1;
                    } else u = members.get(0).getUser();
                }
                if (e == 1) {
                    return "$ERROR$Multiple users found, use ID if possible";
                }
                if (u == null) {
                    return "$ERROR$User not found";
                }
            }
        }
        if (u == null) {
            return "$ERROR$User not found";
        }
        StringBuilder sb = new StringBuilder("```\n");
        Member mbr = m.getGuild().getMember(u);
        Map<String, String> s = new LinkedHashMap<>();
        s.put("name",          u.getName());
        s.put("discriminator", "#" + u.getDiscriminator());
        s.put("id",            u.getId());
        s.put("is a bot",      u.isBot() ? "yes" : "no");
        s.put("created",       u.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME));
        s.put("in this guild", mbr != null ? "yes" : "no");
        if (mbr != null) {
            s.put("nick",        mbr.getNickname() == null ? "no" : "yes, '" + mbr.getNickname() + "'");
            s.put("is owner",    mbr.isOwner() ? "yes" : "no");
            s.put("role amount", Integer.toString(mbr.getRoles().size()));
            s.put("roles",       mbr.getRoles().stream().map(Role::getName).collect(Collectors.joining(", ")));
            s.put("status",      mbr.getOnlineStatus().getKey().toLowerCase());
            s.put("game",        (mbr.getGame() != null ? "'" + mbr.getGame().getName() + "'" : "none"));
            s.put("join date",   mbr.getJoinDate().format(DateTimeFormatter.RFC_1123_DATE_TIME));
        }
        for (String k : s.keySet()) sb.append(k).append(Info.spaces(k)).append(s.get(k)).append("\n");
        return sb.append("\n```").append(u.getAvatarUrl()).toString();
    }
}
