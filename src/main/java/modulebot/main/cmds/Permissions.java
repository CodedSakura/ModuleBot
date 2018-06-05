package modulebot.main.cmds;

import modulebot.main.Main;
import modulebot.main.hosts.Command;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Permissions extends Command {
    @Override
    public String getName() {
        return "perms";
    }

    @Override
    public String getHelp() {
        return "Modifies permission settings";
    }

    @Override
    public String[] getUsages() {
        return new String[] {"list", "add {'ban'|'admin'} [ID...]", "rm {'ban'|'admin'} [ID...]", "reload"};
    }

    @Override
    public void run(Message m) throws Exception {
        String[] args = getArgs(m);
        long gid = m.getGuild().getIdLong(); // TODO: remove my ID from code    vvvvvvvvvvvvvvvvvv
        if (!args[0].equals("list") && !admin && !m.getAuthor().getId().equals("167348844005163009")) {
            send("This command is admin only");
        } else if (args[0].equals("list")) {
            ArrayList<String> banUsers = Main.settings.get(gid).get("banUsers");
            if (banUsers.size() == 1 && banUsers.get(0).equals("")) banUsers.remove(0);
            ArrayList<String> banRoles = Main.settings.get(gid).get("banRoles");
            if (banRoles.size() == 1 && banRoles.get(0).equals("")) banRoles.remove(0);
            ArrayList<String> admUsers = Main.settings.get(gid).get("admUsers");
            if (admUsers.size() == 1 && admUsers.get(0).equals("")) admUsers.remove(0);
            ArrayList<String> admRoles = Main.settings.get(gid).get("admRoles");
            if (admRoles.size() == 1 && admRoles.get(0).equals("")) admRoles.remove(0);
            StringBuilder sb = new StringBuilder();
            if (banUsers.size() > 0) {
                sb.append("Banned users:\n");
                for (String id : banUsers) {
                    User u = m.getJDA().getUserById(id);
                    sb.append(u.getName()).append("#").append(u.getDiscriminator()).append(" ");
                }
                sb.append("\n");
            }
            if (banRoles.size() > 0) {
                sb.append("Banned roles:\n");
                for (String id : banRoles) sb.append("@").append(m.getGuild().getRoleById(id).getName()).append(" ");
                sb.append("\n");
            }
            if (admUsers.size() > 0) {
                sb.append("Admin users:\n");
                for (String id : admUsers) {
                    User u = m.getJDA().getUserById(id);
                    sb.append(u.getName()).append("#").append(u.getDiscriminator()).append(" ");
                }
                sb.append("\n");
            }
            if (admRoles.size() > 0) {
                sb.append("Admin roles:\n");
                for (String id : admRoles) sb.append("@").append(m.getGuild().getRoleById(id).getName()).append(" ");
                sb.append("\n");
            }
            send(sb.length() > 0 ? sb.toString() : "No permissions set up");
        } else if (args[0].equals("reload")) {
            PreparedStatement st = Main.conn.prepareStatement("SELECT * FROM servers WHERE id = ?");
            st.setLong(1, gid);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Main.prefix.put(gid, rs.getString("prefix"));
                HashMap<String, ArrayList<String>> map = new HashMap<>();
                String modules  = rs.getString("modules" );
                String banUsers = rs.getString("banUsers");
                String banRoles = rs.getString("banRoles");
                String admUsers = rs.getString("admUsers");
                String admRoles = rs.getString("admRoles");
                map.put("modules" , new ArrayList<>(Arrays.asList((modules  == null ? "main" : modules ).split(";"))));
                map.put("banUsers", new ArrayList<>(Arrays.asList((banUsers == null ? ""     : banUsers).split(";"))));
                map.put("banRoles", new ArrayList<>(Arrays.asList((banRoles == null ? ""     : banRoles).split(";"))));
                map.put("admUsers", new ArrayList<>(Arrays.asList((admUsers == null ? ""     : admUsers).split(";"))));
                map.put("admRoles", new ArrayList<>(Arrays.asList((admRoles == null ? ""     : admRoles).split(";"))));
                Main.settings.put(gid, map);
            }
            rs.close();
            st.close();
            send("Done!");
        } else if (args[0].equals("add")) {
            if (args.length <= 2) {
                send("Please provide IDs!");
                return;
            }
            switch (args[1]) {
                case "admin": {
                    ArrayList<String> users = Main.settings.get(gid).get("admUsers");
                    if (users.size() == 1 && users.get(0).equals("")) users.remove(0);
                    ArrayList<String> roles = Main.settings.get(gid).get("admRoles");
                    if (roles.size() == 1 && roles.get(0).equals("")) roles.remove(0);
                    if (add(m, args, users, roles)) return;
                    PreparedStatement ps = Main.conn.prepareStatement("UPDATE servers SET admUsers = ?, admRoles = ? WHERE id = ?");
                    ps.setString(1, String.join(";", users));
                    ps.setString(2, String.join(";", roles));
                    ps.setLong(3, gid);
                    ps.execute();
                    Main.settings.get(gid).replace("admUsers", users);
                    Main.settings.get(gid).replace("admRoles", roles);
                    send("Done!");
                    break;
                }
                case "ban": {
                    ArrayList<String> users = Main.settings.get(gid).get("banUsers");
                    if (users.size() == 1 && users.get(0).equals("")) users.remove(0);
                    ArrayList<String> roles = Main.settings.get(gid).get("banRoles");
                    if (roles.size() == 1 && roles.get(0).equals("")) roles.remove(0);
                    if (add(m, args, users, roles)) return;
                    PreparedStatement ps = Main.conn.prepareStatement("UPDATE servers SET banUsers = ?, banRoles = ? WHERE id = ?");
                    ps.setString(1, String.join(";", users));
                    ps.setString(2, String.join(";", roles));
                    ps.setLong(3, gid);
                    ps.execute();
                    Main.settings.get(gid).replace("banUsers", users);
                    Main.settings.get(gid).replace("banRoles", roles);
                    send("Done!");
                    break;
                }
                default:
                    defaultRun();
                    break;
            }
        } else if (args[0].equals("rm")) {
            if (args.length <= 2) {
                send("Please provide IDs!");
                return;
            }
            switch (args[1]) {
                case "admin": {
                    ArrayList<String> users = Main.settings.get(gid).get("admUsers");
                    if (users.size() == 1 && users.get(0).equals("")) users.remove(0);
                    ArrayList<String> roles = Main.settings.get(gid).get("admRoles");
                    if (roles.size() == 1 && roles.get(0).equals("")) roles.remove(0);
                    if (rm(m, args, users, roles)) return;
                    PreparedStatement ps = Main.conn.prepareStatement("UPDATE servers SET admUsers = ?, admRoles = ? WHERE id = ?");
                    ps.setString(1, String.join(";", users));
                    ps.setString(2, String.join(";", roles));
                    ps.setLong(3, gid);
                    ps.execute();
                    Main.settings.get(gid).replace("admUsers", users);
                    Main.settings.get(gid).replace("admRoles", roles);
                    send("Done!");
                    break;
                }
                case "ban": {
                    ArrayList<String> users = Main.settings.get(gid).get("banUsers");
                    if (users.size() == 1 && users.get(0).equals("")) users.remove(0);
                    ArrayList<String> roles = Main.settings.get(gid).get("banRoles");
                    if (roles.size() == 1 && roles.get(0).equals("")) roles.remove(0);
                    if (rm(m, args, users, roles)) return;
                    PreparedStatement ps = Main.conn.prepareStatement("UPDATE servers SET banUsers = ?, banRoles = ? WHERE id = ?");
                    ps.setString(1, String.join(";", users));
                    ps.setString(2, String.join(";", roles));
                    ps.setLong(3, gid);
                    ps.execute();
                    Main.settings.get(gid).replace("banUsers", users);
                    Main.settings.get(gid).replace("banRoles", roles);
                    send("Done!");
                    break;
                }
                default:
                    defaultRun();
                    break;
            }
        } else defaultRun();
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
