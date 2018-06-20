package modulebot.main.cmds;

import modulebot.main.Main;
import modulebot.main.hosts.Command;
import net.dv8tion.jda.core.entities.Message;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;

public class Module extends Command {
    @Override
    public String getName() {
        return "module";
    }

    @Override
    public String getHelp() {
        return "Enables, disables and gives info on modules";
    }

    @Override
    public String[] getUsages() {
        return new String[] {"enable [name]", "disable [name]", "info [name]", "list"};
    }

    @Override
    public void run(Message m) throws Exception {
        String[] args = getArgs(m);
        if (args.length == 0) {
            defaultRun();
            return;
        }
        long gid = m.getGuild().getIdLong();
        if (args[0].equals("info")) {
            if (args.length > 1 && Main.moduleInfo.containsKey(args[1])) {
                send(Main.moduleInfo.get(args[1]));
            } else {
                send("Module not found");
            }
        } else if (args[0].equals("list")) {
            StringBuilder sb = new StringBuilder("```diff\n");
            ArrayList<String> gModules = Main.settings.get(gid).get("modules");
            ArrayList<String> modules = new ArrayList<>(Main.modules.keySet());
            modules.sort(Comparator.comparing(String::toLowerCase));
            for (String module : modules) {
                sb.append(gModules.contains(module) ? "+" : "-").append(" ").append(module).append(" - ")
                        .append(Main.moduleInfo.get(module)).append("\n");
            }
            send(sb.append("```").toString());
        } else if (!admin && (args[0].equals("enable") || args[0].equals("disable"))) {
            send("This command is admin only");
        } else if (args[0].equals("enable")) {
            if (args.length > 1 && Main.modules.containsKey(args[1])) {
                if (Main.settings.get(gid).get("modules").contains(args[1])) {
                    send("Module already enabled");
                } else {
                    Main.settings.get(gid).get("modules").add(args[1]);
                    PreparedStatement st = Main.conn.prepareStatement("UPDATE servers SET modules = ? WHERE id = ?");
                    st.setString(1, String.join(";", Main.settings.get(gid).get("modules")));
                    st.setLong(2, m.getGuild().getIdLong());
                    st.executeUpdate();
                    st.close();
                    send("Module \"" + args[1] + "\" enabled");

                    try {
                        for (String n : Main.settings.get(m.getGuild().getIdLong()).get("modules")) {
                            if (Main.commandHosts.containsKey(n) && n.equals(args[1])) {
                                Main.commandHosts.get(n).onEnabled(gid, m.getTextChannel());
                                Main.commandHosts.get(n).onToggled(gid, m.getTextChannel());
                            }
                        }
                    } catch (ConcurrentModificationException ignored) {
                        m.getChannel().deleteMessageById(messageID).queue();
                    }
                }
            } else {
                send("Module not found");
            }
        } else if (args.length > 1 && args[1].equals("main")) {
            send("You cannot disable the main module");
        } else if (args[0].equals("disable")) {
            if (args.length > 1 && Main.modules.containsKey(args[1])) {
                if (!Main.settings.get(gid).get("modules").contains(args[1])) {
                    send("Module already disabled");
                } else {
                    Main.settings.get(gid).get("modules").remove(args[1]);
                    PreparedStatement st = Main.conn.prepareStatement("UPDATE servers SET modules = ? WHERE id = ?");
                    st.setString(1, String.join(";", Main.settings.get(gid).get("modules")));
                    st.setLong(2, m.getGuild().getIdLong());
                    st.executeUpdate();
                    st.close();
                    send("Module \"" + args[1] + "\" disabled");

                    for (String n : Main.settings.get(m.getGuild().getIdLong()).get("modules")) {
                        if (Main.commandHosts.containsKey(n) && n.equals(args[1])) {
                            Main.commandHosts.get(n).onDisabled(gid, m.getTextChannel());
                            Main.commandHosts.get(n).onToggled(gid, m.getTextChannel());
                        }
                    }
                }
            } else {
                send("Module not found");
            }
        } else {
            send("Unknown/missing arguments");
        }
    }
}
