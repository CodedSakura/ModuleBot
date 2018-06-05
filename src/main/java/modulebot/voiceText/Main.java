package modulebot.voiceText;

import modulebot.main.hosts.Command;
import modulebot.main.hosts.CommandHost;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Main extends CommandHost {
    // TODO: remove v
    private static JSONObject roles;
    private static HashMap<Long, HashMap<String, ArrayList<String>>> settings = new HashMap<>();
    // TODO: remove ^

    @Override
    public Command[] getCommands() {
        return new Command[0];
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
        System.out.println("voiceText READY EVENT");
        try {
            Statement st = modulebot.main.Main.conn.createStatement();
            st.execute("" +
                    "create table if not exists voiceText(" +
                        "id       bigint unsigned not null," +
                        "banUsers text            null," +
                        "banRoles text            null," +
                        "admUsers text            null," +
                        "admRoles text            null" +
                    ") COLLATE utf8_bin engine InnoDB");
            ResultSet rs = st.executeQuery("SELECT * FROM voiceText");
            while (rs.next()) {
                long id = rs.getLong("id");
                HashMap<String, ArrayList<String>> map = new HashMap<>();
                String banUsers = rs.getString("banUsers");
                String banRoles = rs.getString("banRoles");
                String admUsers = rs.getString("admUsers");
                String admRoles = rs.getString("admRoles");
                map.put("banUsers", new ArrayList<>(Arrays.asList((banUsers == null ? ""     : banUsers).split(";"))));
                map.put("banRoles", new ArrayList<>(Arrays.asList((banRoles == null ? ""     : banRoles).split(";"))));
                map.put("admUsers", new ArrayList<>(Arrays.asList((admUsers == null ? ""     : admUsers).split(";"))));
                map.put("admRoles", new ArrayList<>(Arrays.asList((admRoles == null ? ""     : admRoles).split(";"))));
                settings.put(id, map);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void voiceJoin(Channel c, Guild g, Member m) {
        if (c.getMembers().size() < 2) return;

        String name = c.getName().replace(" ", "-") + "-voice";

        if (g.getTextChannelsByName(name, true).size() == 0) {
            Channel ch = g.getController().createTextChannel(name).complete();
            ch.getManager().setParent(c.getParent()).queue();

            ch.getMemberPermissionOverrides().forEach(q -> q.getManagerUpdatable().reset());


            ch.createPermissionOverride(g.getRoleById(roles.getString("member"))).setDeny (Permission.MESSAGE_READ).queue();
            ch.createPermissionOverride(g.getRoleById(roles.getString("admin"))) .setAllow(Permission.MESSAGE_READ).queue();
            ch.createPermissionOverride(g.getRoleById(roles.getString("bot")))   .setAllow(Permission.MESSAGE_READ).queue();

            c.getMembers().forEach(q -> ch.createPermissionOverride(q).setAllow(Permission.MESSAGE_READ).queue());
        } else {
            g.getTextChannelsByName(name, true).get(0).createPermissionOverride(m).setAllow(Permission.MESSAGE_READ).queue();
        }
    }

    private void voiceLeave(VoiceChannel ch, Guild g, Member m) {
        String name = ch.getName().replace(" ", "-") + "-voice";
        List<TextChannel> c = g.getTextChannelsByName(name, true);
        if (c.size() == 1) {
            if (ch.getMembers().size() < 2) c.get(0).delete().queue();
            else c.get(0).getPermissionOverride(m).getManagerUpdatable().getPermissionOverride().delete().queue();
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

    //
    // TODO: move V
    //

    private static JSONObject channels;
    private static JSONObject config;
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (event.getMember().getUser().isBot()) {
            event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRoleById(roles.getString("bot"))).queue();
            event.getGuild().getTextChannelById(channels.getString("default")).sendMessage("A bot (" + event.getMember().getEffectiveName() + ") was added to the guild").queue();
        } else {
            event.getGuild().getController().addRolesToMember(event.getMember(), event.getGuild().getRoleById(roles.getString("member"))).queue();
            event.getGuild().getTextChannelById(channels.getString("default")).sendMessage("" +
                    "Welcome, " + event.getMember().getUser().getAsMention() + "! Please read <#" + channels.getString("readme") + ">!\n" +
                    "To get a language role just do `" + config.getString("role cmd") + "`\n" +
                    "To see a list of available roles, do `" + config.getString("roles cmd") + "`!"
            ).queue();
            event.getGuild().getTextChannelById(channels.getString("admin")).sendMessage(event.getMember().getEffectiveName() + " joined the guild!").queue();
        }
    }
    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        event.getGuild().getTextChannelById(channels.getString("admin")).sendMessage(event.getMember().getEffectiveName() + " left the guild!").queue();
    }
}
