package eu.thephisics101.modulebot.cmds;

import eu.thephisics101.modulebot.Main;
import eu.thephisics101.modulebot.hosts.CH;
import eu.thephisics101.modulebot.hosts.Command;
import eu.thephisics101.modulebot.hosts.CommandHost;
import net.dv8tion.jda.core.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

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
        return new String[]{"list", "info [name]", "enable [name]", "disable [name]", "reload"};
    }

    @Override
    public void run(Message m) throws Exception {
        String[] args = getArgs(m);
        if (args.length == 0) {
            defaultRun();
            return;
        }
        long gid = m.getGuild().getIdLong();
        if ("list".equals(args[0])) {
            StringBuilder sb = new StringBuilder("```diff\n");
            ArrayList<String> gModules = Main.settings.get(gid).get("modules");
            ArrayList<String> modules = new ArrayList<>(Main.modules.keySet());
            modules.sort(Comparator.comparing(String::toLowerCase));
            for (String module : modules) {
                sb.append(gModules.contains(module) ? "+" : "-").append(" ").append(module).append(" - ")
                        .append(Main.moduleInfo.get(module)).append("\n");
            }
            send(sb.append("```").toString());
        } else if ("info".equals(args[0])) {
            if (args.length > 1 && Main.moduleInfo.containsKey(args[1])) {
                send(Main.moduleInfo.get(args[1]));
            } else {
                send("Module not found");
            }
        } else if ("enable".equals(args[0])) {
            if (!admin) {
                send("This command is admin only");
                return;
            }
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
        } else if ("disable".equals(args[0])) {
            if (!admin) {
                send("This command is admin only");
                return;
            }
            if (args.length > 1 && Main.modules.containsKey(args[1])) {
                if (args[1].equals("main")) {
                    send("You cannot disable the main module");
                    return;
                }
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
        } else if ("reload".equals(args[0])) {
            if (!admin) {
                send("This command is admin only");
                return;
            }

            HashMap<String, Command[]> modules = new HashMap<>(Main.modules);
            HashMap<String, String> moduleInfo = new HashMap<>(Main.moduleInfo);
            HashMap<String, CommandHost> commandHosts = new HashMap<>(Main.commandHosts);

            Main.modules.clear();
            Main.moduleInfo.clear();
            Main.commandHosts.clear();

            Main.modules.put(Main.name, Main.commands);
            Main.moduleInfo.put(Main.name, Main.description);

            Logger logger = LoggerFactory.getLogger(this.getClass());
            File[] moduleFiles = new File("./modules").listFiles();
            if (moduleFiles == null) return;
            for (File mf : moduleFiles) {
                if (!mf.getName().endsWith(".jar")) continue;
                logger.debug("Loading from " + mf.getPath());
                URLClassLoader child = new URLClassLoader(new URL[] {mf.toURI().toURL()}, Main.class.getClassLoader());
                for (String className : getClassNames(mf.getPath())) {
                    Class classToLoad = Class.forName(className.substring(0, className.lastIndexOf(".")), true, child);
                    if (!isInstantiable(classToLoad)) continue;
                    Object instance;
                    try { instance = classToLoad.newInstance(); } catch (Exception ignored) { continue; }
                    if (instance instanceof CH) {
                        logger.debug("Loading " + className.substring(0, className.lastIndexOf(".")));
                        CH ch = (CH) instance;
                        if (Main.modules.containsKey(ch.getName().toLowerCase())) continue;
                        if (ch instanceof CommandHost) Main.commandHosts.put(ch.getName().toLowerCase(), (CommandHost) ch);
                        Main.modules.put(ch.getName().toLowerCase(), ch.getCommands());
                        Main.moduleInfo.put(ch.getName().toLowerCase(), ch.getDescription());
                    }
                }
            }
            logger.debug("Done!");

            boolean launchSuccess = true;
            if (Main.modules.size() != Main.moduleInfo.size()) launchSuccess = false;
            for (String mk : Main.modules.keySet()) launchSuccess = launchSuccess && Main.moduleInfo.containsKey(mk) &&
                    mk.trim().split(" ").length == 1;
            for (Command[] cmds : Main.modules.values()) {
                ArrayList<String> e = new ArrayList<>();
                for (Command c : cmds) {
                    String n = c.getName();
                    if (e.contains(n) || n.trim().split(" ").length > 1) launchSuccess = false;
                    e.add(n);
                }
            }

            if (!launchSuccess) {
                send("Reload failure, resetting...");
                Main.modules = modules;
                Main.moduleInfo = moduleInfo;
                Main.commandHosts = commandHosts;
            } else {
                send("Modules reloaded successfully");
            }
        } else send("Unknown arguments");
    }

    private List<String> getClassNames(String jarName) throws IOException {
        ArrayList<String> classes = new ArrayList<>();

        JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
        JarEntry jarEntry;
        while ((jarEntry = jarFile.getNextJarEntry()) != null) {
            if (jarEntry.getName().endsWith(".class")) {
                classes.add(jarEntry.getName().replaceAll("/", "\\."));
            }
        }

        return classes;
    }

    private boolean isInstantiable(Class<?> clz) {
        return !clz.isPrimitive() &&
                !Modifier.isAbstract(clz.getModifiers()) &&
                !clz.isInterface() &&
                !clz.isArray() &&
                !String.class.getName().equals(clz.getName()) &&
                !Integer.class.getName().equals(clz.getName());
    }
}
