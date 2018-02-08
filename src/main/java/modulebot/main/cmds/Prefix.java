package modulebot.main.cmds;

import modulebot.main.Command;
import modulebot.main.Main;
import net.dv8tion.jda.core.entities.Message;

import java.sql.PreparedStatement;

public class Prefix extends Command {
    @Override
    public String getName() {
        return "prefix";
    }

    @Override
    public String getHelp() {
        return "Gets/sets the bot's prefix in the current server";
    }

    @Override
    public String[] getUsages() {
        return new String[] {"{prefix}"};
    }

    @Override
    public void run(Message m) throws Exception {
        String c = trimPrefix(m);
        String[] args = c.split(" ");
        if (args.length != 1 && !admin) {
            send("This command is admin only");
        } else if (args.length == 1) {
            send("Prefix: " + Main.prefix.get(m.getGuild().getIdLong()));
        } else {
            String prefix = c.substring(c.indexOf(" ") + 1);
            PreparedStatement st = Main.conn.prepareStatement("UPDATE servers SET prefix = ? WHERE id = ?");
            st.setString(1, prefix);
            st.setLong(2, m.getGuild().getIdLong());
            st.executeUpdate();
            st.close();
            send("Prefix set to \"" + prefix + "\"");
        }
    }
}
