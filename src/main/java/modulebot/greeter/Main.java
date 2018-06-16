package modulebot.greeter;

import modulebot.main.hosts.Command;
import modulebot.main.hosts.CommandHost;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class Main extends CommandHost {
    HashMap<Long, HashMap<Long, String>> greetings = new HashMap<>();

    @Override
    public Command[] getCommands() {
        return new Command[] { new Configure() };
    }

    @Override
    public String getName() {
        return "greeter";
    }

    @Override
    public String getDescription() {
        return "Fully customisable greeter for newly joined people";
    }

    @Override
    public void onReady(ReadyEvent event) {
        try {
            Statement st = modulebot.main.Main.conn.createStatement();
            st.execute("" +
                    "create table if not exists greeter(" +
                        "id     bigint unsigned not null unique auto_increment primary key," +
                        "gID    bigint unsigned not null," +
                        "chID   bigint unsigned not null," +
                        "text   text            not null," +
                        "target char(1)         not null " +
                    ") COLLATE utf8_bin engine InnoDB");
            ResultSet rs = st.executeQuery("SELECT * FROM greeter");
            while (rs.next()) {
                long gid = rs.getLong("gID");
                long chID = rs.getLong("chID");
                String text = rs.getString("text");
                if (!greetings.containsKey(gid)) greetings.put(gid, new HashMap<>());
                greetings.get(gid).put(chID, text);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnabled(long gid, TextChannel c) {
        if (c != null) {
            c.sendMessage("You can configure guild's greeter by using `" + modulebot.main.Main.prefix.get(gid) + "greeter config {args}`").queue();
        }
    }



    private static JSONObject roles;
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
}
