package modulebot.info;

import modulebot.main.hosts.Command;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class RoleInfo extends Command {
    @Override
    public String getName() {
        return "role";
    }

    @Override
    public String getHelp() {
        return "Gives information about a role";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"[ID]", "[role name]"};
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
        Role r;
        if (args.length == 0) return ERR[0];
        else {
            if (args[0].matches("\\d{17,18}") && m.getGuild().getRoleById(args[0]) != null)
                r = m.getGuild().getRoleById(args[0]);
            else {
                List<Role> roles = m.getGuild().getRolesByName(args[0], true);
                if (m.getMentionedRoles().size() > 0) {
                    if (m.getMentionedRoles().size() > 1) return ERR[1];
                    else r = m.getMentionedRoles().get(0);
                } else if (roles.size() != 0) {
                    if (roles.size() > 1) {
                        List<Role> roles2 = m.getGuild().getRolesByName(args[0], false);
                        if (roles2.size() != 0) {
                            if (roles2.size() > 1) return ERR[1];
                            else r = roles2.get(0);
                        } else return ERR[2];
                    } else r = roles.get(0);
                } else return ERR[2];
            }
        }
        if (r == null) return ERR[2];
        StringBuilder sb = new StringBuilder("```\n");
        Map<String, String> s = new LinkedHashMap<>();
        s.put("name",          r.getName());
        s.put("id",            r.getId());
        s.put("created",       r.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME));
        s.put("position",      Integer.toString(r.getPosition()));
        s.put("color",         "#" + Integer.toHexString(r.getColor().getRGB()));
        s.put("mentionable",   r.isMentionable() ? "yes" : "no");
        s.put("permissions",   Integer.toString(r.getPermissions().size()));
        StringJoiner sj = new StringJoiner(", ");
        for (Permission p : r.getPermissions()) sj.add(p.getName());
        if (r.getPermissions().size() > 0) s.put("list:", sj.toString());
        s.put("member count",       Integer.toString(m.getGuild().getMembersWithRoles(r).size()));
        for (String k : s.keySet()) sb.append(k).append(Info.spaces(k)).append(s.get(k)).append("\n");
        return sb.append("\n```").toString();
    }
}
