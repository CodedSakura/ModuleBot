package eu.thephisics101.modulebot;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import eu.thephisics101.modulebot.cmds.*;
import eu.thephisics101.modulebot.hosts.CH;
import eu.thephisics101.modulebot.hosts.Command;
import eu.thephisics101.modulebot.hosts.CommandHost;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.core.events.emote.EmoteRemovedEvent;
import net.dv8tion.jda.core.events.emote.update.EmoteUpdateNameEvent;
import net.dv8tion.jda.core.events.emote.update.EmoteUpdateRolesEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.*;
import net.dv8tion.jda.core.events.guild.update.*;
import net.dv8tion.jda.core.events.guild.voice.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageEmbedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveAllEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.role.RoleCreateEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.events.role.update.*;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class Main extends ListenerAdapter implements CH {
    public static HashMap<Long, String> prefix = new HashMap<>();
    public static HashMap<Long, HashMap<String, ArrayList<String>>> settings = new HashMap<>(); // users, roles, etc
    public static HashMap<String, Command[]> modules = new HashMap<>();
    public static HashMap<String, String> moduleInfo = new HashMap<>();
    public static HashMap<String, CommandHost> commandHosts = new HashMap<>();
    private static HashMap<Long, LinkedHashMap<String, String>> messages = new HashMap<>();

    private static boolean launchSuccess = true;

    public static Connection conn;

    public static String name;
    public static String description;
    public static Command[] commands;

    public static void main(String[] args) throws LoginException, ClassNotFoundException, IOException {
        if (args.length != 5) {
            System.out.println("Bad arguments!");
            System.out.println("Expecting: token, DB User, DB Pass, DB Name, DB Server");
            return;
        }
        if (!new File("./modules").exists()) {
            System.out.println("'./modules' directory was not found!");
            return;
        }
        ListenerAdapter la = new Main(args[1], args[2], args[3], args[4]);
        if (!launchSuccess) return;
        new JDABuilder(AccountType.BOT).setToken(args[0]).addEventListener(la).build();
    }

    private static void openDB(String user, String pass, String name, String server) throws SQLException {
        MysqlDataSource source = new MysqlDataSource();
        source.setUser(user);
        source.setPassword(pass);
        source.setDatabaseName(name);
        source.setServerName(server);
        source.setAutoReconnect(true);
        conn = source.getConnection();
    }
    private static void closeDB() throws SQLException {
        if (conn != null) conn.close();
    }

    @Override
    public Command[] getCommands() {
        return new Command[] {
                new Help(),
                new Ping(),
                new Prefix(),
                new Permissions(),
                new Module()
        };
    }

    @Override
    public String getName() { return "main"; }

    @Override
    public String getDescription() { return "Hosts main commands"; }

    private Main(String user, String pass, String name, String server) throws IOException, ClassNotFoundException {
//        System.out.println(this.getClass().toString());
        try {
            Main.openDB(user, pass, name, server);
            Statement st = Main.conn.createStatement();
            st.execute("" +
                    "create table if not exists servers(" +
                        "id       bigint unsigned not null unique," +
                        "prefix   text            not null," +
                        "modules  text            null," +
                        "banUsers text            null," +
                        "banRoles text            null," +
                        "admUsers text            null," +
                        "admRoles text            null" +
                    ") COLLATE utf8_bin engine InnoDB");
            ResultSet rs = st.executeQuery("SELECT * FROM servers");
            while (rs.next()) {
                long id = rs.getLong("id");
                prefix.put(id, rs.getString("prefix"));
                HashMap<String, ArrayList<String>> map = new HashMap<>();
                String modules  = rs.getString("modules" );
                String banUsers = rs.getString("banUsers");
                String banRoles = rs.getString("banRoles");
                String admUsers = rs.getString("admUsers");
                String admRoles = rs.getString("admRoles");
                map.put("modules" , new ArrayList<>(Arrays.asList((modules  == null ? "main" : modules.toLowerCase()).split(";"))));
                map.put("banUsers", new ArrayList<>(Arrays.asList((banUsers == null ? ""     : banUsers             ).split(";"))));
                map.put("banRoles", new ArrayList<>(Arrays.asList((banRoles == null ? ""     : banRoles             ).split(";"))));
                map.put("admUsers", new ArrayList<>(Arrays.asList((admUsers == null ? ""     : admUsers             ).split(";"))));
                map.put("admRoles", new ArrayList<>(Arrays.asList((admRoles == null ? ""     : admRoles             ).split(";"))));
                settings.put(id, map);
                messages.put(id, new LinkedHashMap<String, String>() {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                        return size() > 10;
                    }
                });
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Main.name = getName().toLowerCase();
        Main.description = getDescription();
        Main.commands = getCommands();


        modules.put(Main.name, commands);
        moduleInfo.put(Main.name, description);

        Logger logger = LoggerFactory.getLogger(this.getClass());
        File[] moduleFiles = new File("./modules").listFiles();
        if (moduleFiles == null) return;
        for (File m : moduleFiles) {
            if (!m.getName().endsWith(".jar")) continue;
            logger.debug("Loading from " + m.getPath());
            URLClassLoader child = new URLClassLoader(new URL[] {m.toURI().toURL()}, Main.class.getClassLoader());
            for (String className : getClassNames(m.getPath())) {
                Class classToLoad = Class.forName(className.substring(0, className.lastIndexOf(".")), true, child);
                if (!isInstantiable(classToLoad)) continue;
                Object instance;
                try { instance = classToLoad.newInstance(); } catch (Exception ignored) { continue; }
                if (instance instanceof CH) {
                    logger.debug("Loading " + className.substring(0, className.lastIndexOf(".")));
                    CH ch = (CH) instance;
                    if (modules.containsKey(ch.getName().toLowerCase())) continue;
                    if (ch instanceof CommandHost) commandHosts.put(ch.getName().toLowerCase(), (CommandHost) ch);
                    modules.put(ch.getName().toLowerCase(), ch.getCommands());
                    moduleInfo.put(ch.getName().toLowerCase(), ch.getDescription());
                }
            }
        }
        logger.debug("Done!");

        // Checking if everything with the modules is alright
        if (modules.size() != moduleInfo.size()) launchSuccess = false;
        for (String m : modules.keySet()) launchSuccess = launchSuccess && moduleInfo.containsKey(m) &&
                    m.trim().split(" ").length == 1;
        for (Command[] cmds : modules.values()) {
            ArrayList<String> e = new ArrayList<>();
            for (Command c : cmds) {
                String n = c.getName();
                if (e.contains(n) || n.trim().split(" ").length > 1) launchSuccess = false;
                e.add(n);
            }
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        try {
            for (Guild g : event.getJDA().getGuilds()) {
                long gid = g.getIdLong();
                if (!settings.containsKey(gid)) {
                    System.out.println("INITIALISING DB FOR " + g.getName());
                    String owner = g.getOwner().getUser().getId();
                    String botOwner = g.getJDA().asBot().getApplicationInfo().complete().getOwner().getId();
                    if (!owner.equals(botOwner)) owner += ";" + botOwner;
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO servers VALUES (?,'%','main','','',?,'')");
                    ps.setLong(1, gid);
                    ps.setString(2, owner);
                    ps.execute();
                    ps.close();
                    Main.prefix.put(gid, "%");
                    HashMap<String, ArrayList<String>> map = new HashMap<>();
                    map.put("modules" , new ArrayList<>(Collections.singletonList("main")));
                    map.put("banUsers", new ArrayList<>(Collections.singletonList(""    )));
                    map.put("banRoles", new ArrayList<>(Collections.singletonList(""    )));
                    map.put("admUsers", new ArrayList<>(Collections.singletonList(owner )));
                    map.put("admRoles", new ArrayList<>(Collections.singletonList(""    )));
                    Main.settings.put(gid, map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (CommandHost ch : commandHosts.values()) ch.onReady(event);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        try {
            Guild g = event.getGuild();
            long gid = g.getIdLong();
            if (!settings.containsKey(gid)) {
                System.out.println("INITIALISING DB FOR " + g.getName());
                String owner = g.getOwner().getUser().getId();
                String botOwner = g.getJDA().asBot().getApplicationInfo().complete().getOwner().getId();
                if (!owner.equals(botOwner)) owner += ";" + botOwner;
                PreparedStatement ps = conn.prepareStatement("INSERT INTO servers VALUES (?,'%','main','','',?,'')");
                ps.setLong(1, gid);
                ps.setString(2, owner);
                ps.execute();
                ps.close();
                Main.prefix.put(gid, "%");
                HashMap<String, ArrayList<String>> map = new HashMap<>();
                map.put("modules" , new ArrayList<>(Collections.singletonList("main")));
                map.put("banUsers", new ArrayList<>(Collections.singletonList(""    )));
                map.put("banRoles", new ArrayList<>(Collections.singletonList(""    )));
                map.put("admUsers", new ArrayList<>(Collections.singletonList(owner )));
                map.put("admRoles", new ArrayList<>(Collections.singletonList(""    )));
                Main.settings.put(gid, map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildJoin(event);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildMessageReceived(event);

        String content = event.getMessage().getContentRaw();
        SelfUser su = event.getJDA().getSelfUser();
        long gid = event.getGuild().getIdLong();
        String prefix = Main.prefix.get(gid);

        if (!content.startsWith(prefix) && !content.startsWith(su.getAsMention())) return;
        if (settings.get(gid).get("banUsers").contains(event.getAuthor().getId())) return;
        for (Role r : event.getMember().getRoles())
            if (settings.get(gid).get("banRoles").contains(r.getId())) return;

        String[] cmds = content.substring(content.startsWith(prefix) ? prefix.length() : su.getAsMention().length())
                .trim().toLowerCase().split(" ");
        for (int i = 0; i < cmds.length; i++) cmds[i] = cmds[i].trim();

        boolean admin = false;
        if (settings.get(gid).get("admUsers").contains(event.getAuthor().getId())) admin = true;
        for (Role r : event.getMember().getRoles())
            if (settings.get(gid).get("admRoles").contains(r.getId())) admin = true;

        AbstractMap.SimpleEntry<Integer, Command> res = getCommand(cmds, gid);
        Command c = res.getValue();
        if (c != null) {
            messages.get(gid).put(event.getMessageId(), c.execute(event.getMessage(), admin, null));
        } else {
            messages.get(gid).put(event.getMessageId(), event.getChannel().sendMessage(
                    res.getKey() == 1 ? "Command not found" : "Duplicate, please specify module"
            ).complete().getId());
        }
    }
    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildMessageUpdate(event);

        String content = event.getMessage().getContentRaw();
        SelfUser su = event.getJDA().getSelfUser();
        long gid = event.getGuild().getIdLong();
        String prefix = Main.prefix.get(gid);

        if (!content.startsWith(prefix) && !content.startsWith(su.getAsMention())) return;
        if (settings.get(gid).get("banUsers").contains(event.getAuthor().getId())) return;
        for (Role r : event.getMember().getRoles())
            if (settings.get(gid).get("banRoles").contains(r.getId())) return;

        String[] cmds = content.substring(content.startsWith(prefix) ? prefix.length() : su.getAsMention().length())
                .trim().toLowerCase().split(" ");
        for (int i = 0; i < cmds.length; i++) cmds[i] = cmds[i].trim();

        boolean admin = false;
        if (settings.get(gid).get("admUsers").contains(event.getAuthor().getId())) admin = true;
        for (Role r : event.getMember().getRoles())
            if (settings.get(gid).get("admRoles").contains(r.getId())) admin = true;

        AbstractMap.SimpleEntry<Integer, Command> res = getCommand(cmds, gid);
        Command c = res.getValue();
        if (c != null) {
            c.execute(event.getMessage(), admin, messages.get(gid).get(event.getMessageId()));
        } else {
            event.getChannel().editMessageById(messages.get(gid).get(event.getMessageId()),
                    res.getKey() == 1 ? "Command not found" : "Duplicate, please specify module"
            ).queue();
        }
    }

    private static AbstractMap.SimpleEntry<Integer, Command> getCommand(String[] cmds, long gid) {
        ArrayList<String> modulesS = Main.settings.get(gid).get("modules");
        if (cmds.length > 1) for (String module : modulesS) if (module.equals(cmds[0]))
            for (Command c : modules.get(module)) if (c.getName().equals(cmds[1])) return new AbstractMap.SimpleEntry<>(0, c.cloneCMD());
        Command cmd = null;
        for (String module : Main.settings.get(gid).get("modules"))
            for (Command c : modules.get(module))
                if (c.getName().equals(cmds[0])) if (cmd == null) cmd = c.cloneCMD(); else return new AbstractMap.SimpleEntry<>(2, null);
        for (Command c : modules.get("main")) if (c.getName().equals(cmds[0])) return new AbstractMap.SimpleEntry<>(0, c.cloneCMD());
        return new AbstractMap.SimpleEntry<>(cmd == null ? 1 : 0, cmd);
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        try {
            Main.closeDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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


    /*
    * ================================================================
    * =                        EVENT HANDLERS                        =
    * ================================================================
    * */

    @Override
    public void onGuildMessageEmbed(GuildMessageEmbedEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildMessageEmbed(event);
    }
    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildMessageDelete(event);
    }
    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildMessageReactionAdd(event);
    }
    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildMessageReactionRemove(event);
    }
    @Override
    public void onGuildMessageReactionRemoveAll(GuildMessageReactionRemoveAllEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildMessageReactionRemoveAll(event);
    }

    //Guild Events
    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildLeave(event);
    }

    //Guild Update Events
    @Override
    public void onGuildUpdateAfkChannel(GuildUpdateAfkChannelEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildUpdateAfkChannel(event);
    }
    @Override
    public void onGuildUpdateSystemChannel(GuildUpdateSystemChannelEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildUpdateSystemChannel(event);
    }
    @Override
    public void onGuildUpdateAfkTimeout(GuildUpdateAfkTimeoutEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildUpdateAfkTimeout(event);
    }
    @Override
    public void onGuildUpdateExplicitContentLevel(GuildUpdateExplicitContentLevelEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildUpdateExplicitContentLevel(event);
    }
    @Override
    public void onGuildUpdateIcon(GuildUpdateIconEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildUpdateIcon(event);
    }
    @Override
    public void onGuildUpdateMFALevel(GuildUpdateMFALevelEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildUpdateMFALevel(event);
    }
    @Override
    public void onGuildUpdateName(GuildUpdateNameEvent event){
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildUpdateName(event);
    }
    @Override
    public void onGuildUpdateNotificationLevel(GuildUpdateNotificationLevelEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildUpdateNotificationLevel(event);
    }
    @Override
    public void onGuildUpdateOwner(GuildUpdateOwnerEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildUpdateOwner(event);
    }
    @Override
    public void onGuildUpdateRegion(GuildUpdateRegionEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildUpdateRegion(event);
    }
    @Override
    public void onGuildUpdateSplash(GuildUpdateSplashEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildUpdateSplash(event);
    }
    @Override
    public void onGuildUpdateVerificationLevel(GuildUpdateVerificationLevelEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildUpdateVerificationLevel(event);
    }
    @Override
    public void onGuildUpdateFeatures(GuildUpdateFeaturesEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildUpdateFeatures(event);
    }

    //Guild Member Events
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildMemberJoin(event);
    }
    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildMemberLeave(event);
    }
    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildMemberRoleAdd(event);
    }
    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildMemberRoleRemove(event);
    }
    @Override
    public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildMemberNickChange(event);
    }

    //Guild Voice Events
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildVoiceUpdate(event);
    }
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildVoiceJoin(event);
    }
    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildVoiceMove(event);
    }
    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildVoiceLeave(event);
    }
    @Override
    public void onGuildVoiceMute(GuildVoiceMuteEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildVoiceMute(event);
    }
    @Override
    public void onGuildVoiceDeafen(GuildVoiceDeafenEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildVoiceDeafen(event);
    }
    @Override
    public void onGuildVoiceGuildMute(GuildVoiceGuildMuteEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildVoiceGuildMute(event);
    }
    @Override
    public void onGuildVoiceGuildDeafen(GuildVoiceGuildDeafenEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildVoiceGuildDeafen(event);
    }
    @Override
    public void onGuildVoiceSelfMute(GuildVoiceSelfMuteEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildVoiceSelfMute(event);
    }
    @Override
    public void onGuildVoiceSelfDeafen(GuildVoiceSelfDeafenEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildVoiceSelfDeafen(event);
    }
    @Override
    public void onGuildVoiceSuppress(GuildVoiceSuppressEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onGuildVoiceSuppress(event);
    }

    //Role events
    @Override
    public void onRoleCreate(RoleCreateEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onRoleCreate(event);
    }
    @Override
    public void onRoleDelete(RoleDeleteEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onRoleDelete(event);
    }

    //Role Update Events
    @Override
    public void onRoleUpdateColor(RoleUpdateColorEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onRoleUpdateColor(event);
    }
    @Override
    public void onRoleUpdateHoisted(RoleUpdateHoistedEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onRoleUpdateHoisted(event);
    }
    @Override
    public void onRoleUpdateMentionable(RoleUpdateMentionableEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onRoleUpdateMentionable(event);
    }
    @Override
    public void onRoleUpdateName(RoleUpdateNameEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onRoleUpdateName(event);
    }
    @Override
    public void onRoleUpdatePermissions(RoleUpdatePermissionsEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onRoleUpdatePermissions(event);
    }
    @Override
    public void onRoleUpdatePosition(RoleUpdatePositionEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onRoleUpdatePosition(event);
    }

    //Emote Events
    @Override
    public void onEmoteAdded(EmoteAddedEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onEmoteAdded(event);
    }
    @Override
    public void onEmoteRemoved(EmoteRemovedEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onEmoteRemoved(event);
    }

    //Emote Update Events
    @Override
    public void onEmoteUpdateName(EmoteUpdateNameEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onEmoteUpdateName(event);
    }
    @Override
    public void onEmoteUpdateRoles(EmoteUpdateRolesEvent event) {
        for (String m : Main.settings.get(event.getGuild().getIdLong()).get("modules"))
            if (commandHosts.containsKey(m)) commandHosts.get(m).onEmoteUpdateRoles(event);
    }
}
