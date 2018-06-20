package modulebot.greeter;

import modulebot.main.Main;
import modulebot.main.hosts.Command;
import net.dv8tion.jda.core.entities.Message;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Configure extends Command {

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getHelp() {
        return "Configures settings for greeter";
    }

    @Override
    public String[] getUsages() {
        return new String[] {"help", "add [args]", "rm [id]", "list"};
    }

    @Override
    public void run(Message m) throws Exception {
        String[] args = getArgs(m);
        String helpText = "The greeter separates users and bots, so you can set up different greetings for each of them\n" +
                "Use `greeter config add-greet u [channel] [text]` to configure a greeting for users\n" +
                "Use `greeter config add-greet b [channel] [text]` to configure it for bots\n" +
                "You can have multiple greetings for each, if they're in the same channel, one random appropriate greeting\n" +
                "\n" +
                "Greetings use a small and simple formatting language, that has the following:\n" +
                "`{{name}}` - gets the name of the one that joined\n" +
                "`{{mention}}` - mention version of `$NAME$`\n" +
                "`{{time}}` - current GMT time and date\n" +
                "`{{guild}}` - name of the guild (server)\n" +
                "\n" +
                "Example:\n" +
                "If you want to create 2 user greetings in #general, one of which would be randomly selected when someone joins,\n" +
                "and a bot greeting (not really a greeting) in #bots, you would run these commands (in any order): ```\n" +
                "greeter config add-greet u #general Welcome {{mention}}, please have a read of #readme!\n" +
                "greeter config add-greet u #general {{mention}}, you have been randomly selected to be welcome in {{guild}}!\n" +
                "greeter config add-greet b #bots We have a new bot, {{name}}, added at {{time}}\n" +
                "```(by #general, #readme, etc. are meant channel mentions or IDs)";
        if (!admin) {
            send("This command is admin only");
        } else if (args.length < 1) {
            defaultRun();
        } else if (args[0].equals("help")) {
            send(helpText);
        } else if (args[0].equals("list")) {
            PreparedStatement ps = Main.conn.prepareStatement("SELECT id, chID, text, target from greeter where gID = ?");
            ps.setLong(1, m.getGuild().getIdLong());
            ResultSet rs = ps.executeQuery();
            ArrayList<String[]> data = new ArrayList<>();
            while (rs.next()) {
                data.add(new String[] {
                        Long.toString(rs.getLong("id")),
                        m.getGuild().getTextChannelById(rs.getLong("chID")).getName(),
                        rs.getString("target").equals("u") ? "users" : "bots",
                        rs.getString("text")
                });
            }
            rs.close();
            ps.close();
            if (data.size() == 0) {
                send("No greetings found");
                return;
            }
            data.add(0, new String[] {"ID", "Channel", "Target", "Text"});
            int[] mLen = new int[2];
            for (String[] g : data) for (int i = 0; i < 2; i++) if (mLen[i] < g[i].length()) mLen[i] = g[i].length();
            StringBuilder sb = new StringBuilder("```\n");
            for (String[] g : data)
                sb.append(pad(g[0], mLen[0] + 2)).append(pad(g[1], mLen[1] + 2)).append(pad(g[2], 8))
                        .append(shorten(g[3])).append("\n");
            send(sb.append("```").toString());
        } else if (args[0].equals("add")) {
            if (args.length < 3 || !args[1].matches("[ub]")) {
                send(helpText);
                return;
            }
            String target = args[1];
            String ch = args[2];
            if (ch.startsWith("<#")) {
                ch = ch.substring(2, args[2].length() - 1);
            }
            if (!ch.matches("\\d{17,18}") || m.getGuild().getTextChannelById(ch) == null) {
                send(helpText);
                return;
            }
            String text = getArg(m).substring(args[0].length() + args[1].length() + args[2].length() + 3);
            PreparedStatement ps = Main.conn.prepareStatement("INSERT INTO greeter VALUES (null, ?, ?, ?, ?)");
            ps.setLong(1, m.getGuild().getIdLong());
            ps.setLong(2, Long.parseLong(ch));
            ps.setString(3, text);
            ps.setString(4, target);
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            send("Added greeting, id: " + rs.getInt(1));
            rs.close();
            ps.close();
        } else if (args[0].equals("rm")) {
            PreparedStatement ps = Main.conn.prepareStatement("DELETE FROM greeter WHERE id = ? and gID = ?");
            ps.setLong(1, Long.parseLong(args[1]));
            ps.setLong(2, m.getGuild().getIdLong());
            if (ps.executeUpdate() == 0) send("ID not found in this guild");
            else send("Greeting deleted successfully");
            ps.close();
        } else defaultRun();
    }

    private String pad(String s, int l) {
        StringBuilder sBuilder = new StringBuilder(s);
        while (sBuilder.length() < l) sBuilder.append(" ");
        return sBuilder.toString();
    }

    private String shorten(String s) {
        int l = 48;
        if (s.length() <= l) return s;
        return s.substring(0, l - 3) + "...";
    }
}
