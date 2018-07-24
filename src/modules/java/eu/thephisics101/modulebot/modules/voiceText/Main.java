package eu.thephisics101.modulebot.modules.voiceText;

import eu.thephisics101.modulebot.hosts.Command;
import eu.thephisics101.modulebot.hosts.CommandHost;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.requests.restaction.ChannelAction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main extends CommandHost {
    static HashMap<Long, HashMap<String, ArrayList<String>>> settings = new HashMap<>();

    @Override
    public Command[] getCommands() {
        return new Command[] { new Configure() };
    }

    @Override
    public String getName() {
        return "voice-text";
    }

    @Override
    public String getDescription() {
        return "Creates text channels when 2 or more people are in a voice channel";
    }

    @Override
    public void onReady(ReadyEvent event) {
        try {
            Statement st = eu.thephisics101.modulebot.Main.conn.createStatement();
            st.execute("" +
                    "create table if not exists voiceText(" +
                        "id            bigint unsigned not null unique," +
                        "disallowUsers text            null," +
                        "disallowRoles text            null," +
                        "alwaysUsers   text            null," +
                        "alwaysRoles   text            null" +
                    ") COLLATE utf8_bin engine InnoDB");
            ResultSet rs = st.executeQuery("SELECT * FROM voiceText");
            while (rs.next()) {
                long id = rs.getLong("id");
                HashMap<String, ArrayList<String>> map = new HashMap<>();
                String disallowUsers = rs.getString("disallowUsers");
                String disallowRoles = rs.getString("disallowRoles");
                String alwaysUsers   = rs.getString("alwaysUsers");
                String alwaysRoles   = rs.getString("alwaysRoles");
                map.put("disallowUsers", new ArrayList<>(Arrays.asList((disallowUsers == null ? "" : disallowUsers).split(";"))));
                map.put("disallowRoles", new ArrayList<>(Arrays.asList((disallowRoles == null ? "" : disallowRoles).split(";"))));
                map.put("alwaysUsers",   new ArrayList<>(Arrays.asList((alwaysUsers   == null ? "" : alwaysUsers)  .split(";"))));
                map.put("alwaysRoles",   new ArrayList<>(Arrays.asList((alwaysRoles   == null ? "" : alwaysRoles)  .split(";"))));
                settings.put(id, map);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void voiceJoin(VoiceChannel c, Guild g, Member m) {
        if (c.getMembers().size() < 2 || !g.getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)) return;

        if (g.getTextChannelsByName(c.getName().replace(" ", "-") + "-voice", true).size() != 0) return;

        String name = c.getName().replace(" ", "-") + "-voice";
        long gid = g.getIdLong();

        if (g.getTextChannelsByName(name, true).size() == 0) {
            ChannelAction ch = g.getController().createTextChannel(name).setParent(c.getParent())
                    .addPermissionOverride(g.getPublicRole(), 0, Permission.MESSAGE_READ.getRawValue());

            List<String> alwaysUsers = settings.get(gid).get("alwaysUsers");
            if (alwaysUsers.size() != 1 || !alwaysUsers.get(0).equals("")) for (String id : alwaysUsers)
                ch = ch.addPermissionOverride(g.getMemberById(id), Permission.MESSAGE_READ.getRawValue(), 0);

            List<String> alwaysRoles = settings.get(gid).get("alwaysRoles");
            if (alwaysRoles.size() != 1 || !alwaysRoles.get(0).equals("")) for (String id : alwaysRoles)
                ch = ch.addPermissionOverride(g.getRoleById(id), Permission.MESSAGE_READ.getRawValue(), 0);

            for (Member mbr : c.getMembers())
                ch = ch.addPermissionOverride(mbr, Permission.MESSAGE_READ.getRawValue(), 0);

            ch.queue();
        } else {
            if (settings.get(gid).get("disallowUsers").contains(m.getUser().getId())) return;
            for (Role r : m.getRoles())
                if (settings.get(gid).get("disallowRoles").contains(r.getId())) return;
            g.getTextChannelsByName(name, true).get(0).createPermissionOverride(m).setAllow(Permission.MESSAGE_READ).queue();
        }
    }

    private void voiceLeave(VoiceChannel ch, Guild g, Member m) {
        if (!g.getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)) return;
        String name = ch.getName().replace(" ", "-") + "-voice";
        List<TextChannel> c = g.getTextChannelsByName(name, true);
        if (c.size() == 1) {
            if (ch.getMembers().size() < 2) c.get(0).delete().queue();
            else c.get(0).getPermissionOverride(m).getManager().getPermissionOverride().delete().queue();
        }
    }


    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        voiceJoin(event.getChannelJoined(), event.getGuild(), event.getMember());
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        voiceLeave(event.getChannelLeft(), event.getGuild(), event.getMember());
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        voiceLeave(event.getChannelLeft(), event.getGuild(), event.getMember());
        voiceJoin(event.getChannelJoined(), event.getGuild(), event.getMember());
    }

    @Override
    public void onEnabled(long gid, TextChannel c) {
        if (!eu.thephisics101.modulebot.modules.voiceText.Main.settings.containsKey(gid)) {
            try {
                PreparedStatement st = eu.thephisics101.modulebot.Main.conn.prepareStatement("INSERT INTO voiceText VALUES (?, '', '', '', '')");
                st.setLong(1, gid);
                st.execute();
                st.close();

                if (c != null) {
                    c.sendMessage("You should configure guild's voice-text settings using `" + eu.thephisics101.modulebot.Main.prefix.get(gid) + "voice-text config {args}`").queue();
                }
            } catch (SQLException e) {
                if (c != null) {
                    c.sendMessage("Error setting up database, please contact bot owner: \n" +
                            c.getJDA().asBot().getApplicationInfo().complete().getOwner().getAsMention() +
                            "**ERROR**: command threw a " + e.getClass().getSimpleName()).queue();
                }
                e.printStackTrace();
            }
        }

        if (c != null) {
            if (!c.getGuild().getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                c.sendMessage("The bot needs the Manage Channels permission to work correctly").queue();
            }
        }
    }
}
