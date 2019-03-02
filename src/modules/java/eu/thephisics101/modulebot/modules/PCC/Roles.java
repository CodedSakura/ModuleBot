package eu.thephisics101.modulebot.modules.PCC;

import eu.thephisics101.modulebot.hosts.Command;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Roles extends Command {
    @Override
    public String getName() {
        return "roles";
    }

    @Override
    public String getHelp() {
        return "Gives and lists language roles\n`change` automatically decides which to add and which to remove";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"list", "change [list of roles]", "who-has [role|\"none\"]"};
    }

    @Override
    public void run(Message m) {
        String[] args = getArgs(m);
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].replaceAll("_", " ");
        }
        if (args.length < 1) {
            defaultRun();
        } else if (args[0].equals("list")) {
            List<Role> mr = m.getMember().getRoles();
            ArrayList<Role> roles = roles(m.getGuild());
            roles.sort(Comparator.comparing(role -> role.getName().toLowerCase()));
            StringBuilder sb = new StringBuilder("```diff\n");
            for (Role r : roles)
                sb.append(mr.contains(r) ? "+" : "-").append(" ").append(r.getName().replaceAll(" ", "_")).append("\n");
            send(sb.append("```").toString());
        } else if (args[0].equals("who-has")) {
            if (args.length < 2) {
                defaultRun();
            } else if (args[1].equals("none")) {
                ArrayList<Member> noRole = new ArrayList<>(m.getGuild().getMembers());
                noRole.removeIf(member -> member.getRoles().contains(m.getGuild().getRoleById("213312815954526208")));
                int a = noRole.size();
                noRole.removeIf(member -> member.getRoles().size() > 1);
                noRole.sort(Comparator.comparing(o -> o.getUser().getName().toLowerCase()));
                StringBuilder sb = new StringBuilder().append(noRole.size()).append(" out of ").append(a)
                        .append(" users don't have a language role:\n");
                noRole.forEach(
                        member -> sb.append("\t").append(member.getUser().getName()).append("#")
                                .append(member.getUser().getDiscriminator()).append("\n")
                );
                send(sb.toString());
            } else {
                Role r;
                if (m.getMentionedRoles().size() > 0)
                    r = m.getMentionedRoles().get(0);
                else if (m.getGuild().getRolesByName(args[1], true).size() > 0)
                    r = m.getGuild().getRolesByName(args[1], true).get(0);
                else if (args[1].matches("\\d{17,18}") && m.getGuild().getRoleById(args[1]) != null)
                    r = m.getGuild().getRoleById(args[1]);
                else {
                    send("please provide a valid role to sort by!");
                    return;
                }

                ArrayList<Member> hasRole = new ArrayList<>(m.getGuild().getMembersWithRoles(r));
                hasRole.sort(Comparator.comparing(o -> o.getUser().getName().toLowerCase()));
                StringBuilder sb = new StringBuilder();
                hasRole.forEach(
                        member -> sb.append(member.getUser().getName()).append("#")
                                .append(member.getUser().getDiscriminator()).append("\n")
                );
                send(sb.toString());
            }
        } else if (args[0].equals("change")) {
            if (args.length < 2) {
                defaultRun();
                return;
            }
            List<Role> toRM = new ArrayList<>();
            for (int i = 1; i < args.length; i++) {
                String arg = args[i];
                if (m.getGuild().getRolesByName(arg, true).size() > 0) {
                    toRM.addAll(m.getGuild().getRolesByName(arg, true));
                } else {
                    send("The role \"" + arg + "\" does not exist");
                    return;
                }
            }
            toRM.removeIf(role -> !roles(m.getGuild()).contains(role));
            List<Role> toADD = new ArrayList<>(toRM);
            toRM.removeIf(role -> !m.getMember().getRoles().contains(role));
            toADD.removeIf(role -> m.getMember().getRoles().contains(role));
            toRM.sort(Comparator.comparing(role -> role.getName().toLowerCase()));
            toADD.sort(Comparator.comparing(role -> role.getName().toLowerCase()));
            m.getGuild().getController().modifyMemberRoles(m.getMember(), toADD, toRM).queue();
            StringBuilder added = new StringBuilder("You now have the role").append(s(toADD.size())).append(": ");
            StringBuilder removed = new StringBuilder("You no longer have the role").append(s(toRM.size())).append(": ");
            for (Role role1 : toADD) added.append("`").append(role1.getName()).append("`, ");
            for (Role role : toRM) removed.append("`").append(role.getName()).append("`, ");
            if (added.length() > 2) added.reverse().delete(0, 2).reverse();
            if (removed.length() > 2) removed.reverse().delete(0, 2).reverse();
            send("" +
                    (toADD.size() > 0 ? added.append("\n").toString() : "") +
                    (toRM.size() > 0 ? removed.toString() : "")
            );
        }
    }

    private ArrayList<Role> roles(Guild g) {
        ArrayList<Role> out = new ArrayList<>(g.getRoles());
        for (String s : new String[] {
                "145570586775519232", // Administrators
                "213312815954526208", // Bot
                "145578521702432768", // Members
                "145571481710100480", // Owner
                "278264459296899072", // Aurora Bot
                "347129489618239499", // MathBot
                "476716097865777163", // BobbyBot
        }) {
            out.remove(g.getRoleById(s));
        }
        out.remove(g.getPublicRole());
        return out;
    }

    private String s(int num) {
        return (num % 10 == 1 && num % 100 != 11) ? "" : "s";
    }
}
