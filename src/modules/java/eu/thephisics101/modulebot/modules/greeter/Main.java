package eu.thephisics101.modulebot.modules.greeter;

import eu.thephisics101.modulebot.hosts.Command;
import eu.thephisics101.modulebot.hosts.CommandHost;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

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
            Statement st = eu.thephisics101.modulebot.Main.conn.createStatement();
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
            c.sendMessage("You can configure guild's greeter by using `" + eu.thephisics101.modulebot.Main.prefix.get(gid) + "greeter config {args}`").queue();
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Guild g = event.getGuild();
        try {
            PreparedStatement ps = eu.thephisics101.modulebot.Main.conn.prepareStatement("SELECT chID, text from greeter where gID = ? and target = ?");
            ps.setLong(1, g.getIdLong());
            ps.setString(2, event.getMember().getUser().isBot() ? "b" : "u");
            ResultSet rs = ps.executeQuery();
            Map<Long, ArrayList<String>> greets = new HashMap<>();
            while (rs.next()) {
                long chID = rs.getLong("chID");
                String text = rs.getString("text");
                if (!greets.containsKey(chID)) greets.put(chID, new ArrayList<>());
                greets.get(chID).add(text);
            }
            rs.close();
            ps.close();
            if (greets.size() == 0) return;
            Random r = new Random();
            SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyy, HH:mm:ss z");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            for (Long chID : greets.keySet()) {
                ArrayList<String> texts = greets.get(chID);
                String text;
                if (texts.size() > 1) text = texts.get(r.nextInt(texts.size()));
                else text = texts.get(0);
                text = text.replaceAll("(?i)\\{\\{name}}", event.getUser().getName())
                        .replaceAll("(?i)\\{\\{mention}}", event.getUser().getAsMention())
                        .replaceAll("(?i)\\{\\{time}}", format.format(new Date()))
                        .replaceAll("(?i)\\{\\{guild}}", g.getName());
                g.getTextChannelById(chID).sendMessage(text).queue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
