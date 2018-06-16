package modulebot.main.cmds;

import modulebot.main.hosts.Command;
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
        String[] args = getArg(m).split(" ");
        if (!args[0].equals("") && !admin) {
            send("This command is admin only");
        } else if (args[0].equals("")) {
            send("Prefix: " + Main.prefix.get(m.getGuild().getIdLong()));
        } else {
            PreparedStatement st = Main.conn.prepareStatement("UPDATE servers SET prefix = ? WHERE id = ?");
            st.setString(1, args[0]);
            st.setLong(2, m.getGuild().getIdLong());
            st.executeUpdate();
            st.close();
            send("Prefix set to \"" + args[0] + "\"");
            Main.prefix.replace(m.getGuild().getIdLong(), args[0]);
        }
    }
}
