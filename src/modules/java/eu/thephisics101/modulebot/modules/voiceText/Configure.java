package eu.thephisics101.modulebot.modules.voiceText;

import eu.thephisics101.modulebot.Main;
import eu.thephisics101.modulebot.hosts.Command;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Configure extends Command {
    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getHelp() {
        return "Configures settings for voice-text module";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"list", "add {'disallow'|'always'} [ID...]", "rm {'disallow'|'always'} [ID...]", "reload"};
    }

    @Override
    public void run(Message m) throws Exception {
        String[] args = getArgs(m);
        long gid = m.getGuild().getIdLong();
        if (args.length == 0) {
            defaultRun();
            return;
        } else if (!args[0].equals("list") && !admin) {
            send("This command is admin only");
            return;
        }
        switch (args[0]) {
            case "list": {
                ArrayList<String> banUsers = eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).get("disallowUsers");
                if (banUsers.size() == 1 && banUsers.get(0).equals("")) banUsers.remove(0);
                ArrayList<String> banRoles = eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).get("disallowRoles");
                if (banRoles.size() == 1 && banRoles.get(0).equals("")) banRoles.remove(0);
                ArrayList<String> admUsers = eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).get("alwaysUsers");
                if (admUsers.size() == 1 && admUsers.get(0).equals("")) admUsers.remove(0);
                ArrayList<String> admRoles = eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).get("alwaysRoles");
                if (admRoles.size() == 1 && admRoles.get(0).equals("")) admRoles.remove(0);
                StringBuilder sb = new StringBuilder();
                if (banUsers.size() > 0) {
                    sb.append("Users that can never view the voice-text channel:\n");
                    for (String id : banUsers) {
                        User u = m.getJDA().getUserById(id);
                        sb.append(u.getName()).append("#").append(u.getDiscriminator()).append(" ");
                    }
                    sb.append("\n");
                }
                if (banRoles.size() > 0) {
                    sb.append("Roles that can never view the voice-text channel:\n");
                    for (String id : banRoles)
                        sb.append("@").append(m.getGuild().getRoleById(id).getName()).append(" ");
                    sb.append("\n");
                }
                if (admUsers.size() > 0) {
                    sb.append("Users that always can view the voice-text channel:\n");
                    for (String id : admUsers) {
                        User u = m.getJDA().getUserById(id);
                        sb.append(u.getName()).append("#").append(u.getDiscriminator()).append(" ");
                    }
                    sb.append("\n");
                }
                if (admRoles.size() > 0) {
                    sb.append("Roles that always can view the voice-text channel:\n");
                    for (String id : admRoles)
                        sb.append("@").append(m.getGuild().getRoleById(id).getName()).append(" ");
                    sb.append("\n");
                }
                send(sb.length() > 0 ? sb.toString() : "No permissions set up");
                break;
            }
            case "reload": {
                PreparedStatement st = Main.conn.prepareStatement("SELECT * FROM voiceText WHERE id = ?");
                st.setLong(1, gid);
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    HashMap<String, ArrayList<String>> map = new HashMap<>();
                    String banUsers = rs.getString("banUsers");
                    String banRoles = rs.getString("banRoles");
                    String admUsers = rs.getString("admUsers");
                    String admRoles = rs.getString("admRoles");
                    map.put("banUsers", new ArrayList<>(Arrays.asList((banUsers == null ? "" : banUsers).split(";"))));
                    map.put("banRoles", new ArrayList<>(Arrays.asList((banRoles == null ? "" : banRoles).split(";"))));
                    map.put("admUsers", new ArrayList<>(Arrays.asList((admUsers == null ? "" : admUsers).split(";"))));
                    map.put("admRoles", new ArrayList<>(Arrays.asList((admRoles == null ? "" : admRoles).split(";"))));
                    eu.thephisics101.modulebot.modules.voiceText.Main.settings.put(gid, map);
                }
                rs.close();
                st.close();
                send("Done!");
                break;
            }
            case "add": {
                if (args.length <= 2) {
                    send("Please provide IDs!");
                    return;
                }
                switch (args[1]) {
                    case "always": {
                        ArrayList<String> users = eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).get("alwaysUsers");
                        if (users.size() == 1 && users.get(0).equals("")) users.remove(0);
                        ArrayList<String> roles = eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).get("alwaysRoles");
                        if (roles.size() == 1 && roles.get(0).equals("")) roles.remove(0);
                        if (add(m, args, users, roles)) return;
                        PreparedStatement ps = Main.conn.prepareStatement("UPDATE voiceText SET alwaysUsers = ?, alwaysRoles = ? WHERE id = ?");
                        ps.setString(1, String.join(";", users));
                        ps.setString(2, String.join(";", roles));
                        ps.setLong(3, gid);
                        ps.execute();
                        ps.close();
                        eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).replace("alwaysUsers", users);
                        eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).replace("alwaysRoles", roles);
                        send("Done!");
                        break;
                    }
                    case "disallow": {
                        ArrayList<String> users = Main.settings.get(gid).get("disallowUsers");
                        if (users.size() == 1 && users.get(0).equals("")) users.remove(0);
                        ArrayList<String> roles = Main.settings.get(gid).get("disallowRoles");
                        if (roles.size() == 1 && roles.get(0).equals("")) roles.remove(0);
                        if (add(m, args, users, roles)) return;
                        PreparedStatement ps = Main.conn.prepareStatement("UPDATE voiceText SET disallowUsers = ?, disallowRoles = ? WHERE id = ?");
                        ps.setString(1, String.join(";", users));
                        ps.setString(2, String.join(";", roles));
                        ps.setLong(3, gid);
                        ps.execute();
                        ps.close();
                        eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).replace("disallowUsers", users);
                        eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).replace("disallowRoles", roles);
                        send("Done!");
                        break;
                    }
                    default:
                        defaultRun();
                        break;
                }
                break;
            }
            case "rm": {
                if (args.length <= 2) {
                    send("Please provide IDs!");
                    return;
                }
                switch (args[1]) {
                    case "always": {
                        ArrayList<String> users = eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).get("alwaysUsers");
                        if (users.size() == 1 && users.get(0).equals("")) users.remove(0);
                        ArrayList<String> roles = eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).get("alwaysRoles");
                        if (roles.size() == 1 && roles.get(0).equals("")) roles.remove(0);
                        if (rm(m, args, users, roles)) return;
                        PreparedStatement ps = Main.conn.prepareStatement("UPDATE voiceText SET alwaysUsers = ?, alwaysRoles = ? WHERE id = ?");
                        ps.setString(1, String.join(";", users));
                        ps.setString(2, String.join(";", roles));
                        ps.setLong(3, gid);
                        ps.execute();
                        eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).replace("alwaysUsers", users);
                        eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).replace("alwaysRoles", roles);
                        send("Done!");
                        break;
                    }
                    case "disallow": {
                        ArrayList<String> users = Main.settings.get(gid).get("disallowUsers");
                        if (users.size() == 1 && users.get(0).equals("")) users.remove(0);
                        ArrayList<String> roles = Main.settings.get(gid).get("disallowRoles");
                        if (roles.size() == 1 && roles.get(0).equals("")) roles.remove(0);
                        if (rm(m, args, users, roles)) return;
                        PreparedStatement ps = Main.conn.prepareStatement("UPDATE voiceText SET disallowUsers = ?, disallowRoles = ? WHERE id = ?");
                        ps.setString(1, String.join(";", users));
                        ps.setString(2, String.join(";", roles));
                        ps.setLong(3, gid);
                        ps.execute();
                        eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).replace("disallowUsers", users);
                        eu.thephisics101.modulebot.modules.voiceText.Main.settings.get(gid).replace("disallowRoles", roles);
                        send("Done!");
                        break;
                    }
                    default:
                        defaultRun();
                        break;
                }
                break;
            }
            default:
                defaultRun();
                break;
        }
    }

    private boolean add(Message m, String[] args, ArrayList<String> users, ArrayList<String> roles) {
        for (int i = 2; i < args.length; i++) {
            if (m.getJDA().getUserById(args[i]) != null) {
                if (!users.contains(args[i])) users.add(args[i]);
            } else if (m.getGuild().getRoleById(args[i]) != null) {
                if (!roles.contains(args[i])) roles.add(args[i]);
            } else {
                send("Please provide only IDs!");
                return true;
            }
        }
        return false;
    }

    private boolean rm(Message m, String[] args, ArrayList<String> users, ArrayList<String> roles) {
        for (int i = 2; i < args.length; i++) {
            if (m.getJDA().getUserById(args[i]) != null) {
                users.remove(args[i]);
            } else if (m.getGuild().getRoleById(args[i]) != null) {
                roles.remove(args[i]);
            } else {
                send("Please provide only IDs!");
                return true;
            }
        }
        return false;
    }
}
