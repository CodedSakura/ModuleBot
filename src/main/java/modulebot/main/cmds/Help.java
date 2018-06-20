package modulebot.main.cmds;

import modulebot.main.hosts.Command;
import modulebot.main.Main;
import net.dv8tion.jda.core.entities.Message;

import java.util.ArrayList;

public class Help extends Command {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return  "Gives information about a command\n\n" +
                "This bot will update recent bot command message edits\n" +
                "Usage syntax: {optional argument}, [required argument]\n";
    }

    @Override
    public String[] getUsages() {
        return new String[] {"list", "{command}", "[module] [command]"};
    }

    @Override
    public void run(Message m) {
        String[] args = getArgs(m);
        long id = m.getGuild().getIdLong();
        if (args.length == 0) defaultRun();
        else if (args.length == 1) {
            if (args[0].equals("list")) {
                StringBuilder sb = new StringBuilder("```\n");
                for (String module : Main.settings.get(id).get("modules")) {
                    sb.append(module).append("\n");
                    for (Command c : Main.modules.get(module)) {
                        sb.append("\t").append(c.getName()).append("\n");
                    }
                }
                send(sb.append("```").toString());
                return;
            }
            ArrayList<Command> commands = new ArrayList<>();
            StringBuilder modules = new StringBuilder();
            for (String module : Main.settings.get(id).get("modules")) {
                for (Command c : Main.modules.get(module)) {
                    if (c.getName().equals(args[0])) {
                        commands.add(c);
                        modules.append("`").append(module).append("`, ");
                    }
                }
            }
            modules.setLength(Math.max(modules.length() - 2, 0));
            if (commands.size() == 0) send("Nothing was found to match `" + args[0] + "`");
            else if (commands.size() > 1) {
                send("Multiple matches, please do `" + Main.prefix.get(id) + "help [module] " + args[0] + "`\n" +
                        "Modules containing it: " + modules.toString());
            } else {
                commands.get(0).setChannel(m.getTextChannel()).defaultRun();
            }
        } else if (args.length == 2) {
            if (!Main.modules.containsKey(args[0])) {
                send("Module named " + args[0] + " was not found");
                return;
            }
            for (Command c : Main.modules.get(args[0])) {
                if (c.getName().equals(args[1])) {
                    c.defaultRun();
                }
            }
        } else {
            send("Too many arguments");
        }
    }
}
