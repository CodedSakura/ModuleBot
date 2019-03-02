package eu.thephisics101.modulebot.modules.calc;

import eu.thephisics101.modulebot.modules.calc.parser.Parser;
import eu.thephisics101.modulebot.modules.calc.parser.exceptions.ParserException;
import eu.thephisics101.modulebot.hosts.Command;
import net.dv8tion.jda.core.entities.Message;

public class Var extends Command {
    @Override
    public String getName() {
        return "var";
    }

    @Override
    public String getHelp() {
        return "Sets, gets and lists Calc's variables";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"list", "set [name] [expression]", "clear [name]", "get [name]"};
    }

    @Override
    public void run(Message m) {
        long gid = m.getGuild().getIdLong();
        if (!Main.parser.containsKey(gid)) Main.parser.put(gid, new Parser());
        String[] args = getArg(m).split(" ");
        switch (args[0]) {
            case "list": {
                if (Main.parser.get(gid).variables.size() > 0) {
                    StringBuilder sb = new StringBuilder("```\n");
                    for (String key : Main.parser.get(gid).variables.keySet()) {
                        sb.append(key).append(" = ").append(Main.parser.get(gid).variables.get(key)).append("\n");
                    }
                    send(sb.append("```").toString());
                } else send("No variables set");
                break;
            }
            case "clear": {
                if (args.length < 2) defaultRun();
                else if (Main.parser.get(gid).variables.containsKey(args[1])) {
                    Main.parser.get(gid).variables.remove(args[1]);
                    send("Variable cleared");
                } else send("Variable not set");
                break;
            }
            case "set": {
                if (args.length < 3) defaultRun();
                else {
                    try {
                        String expr = getArg(m).substring(args[0].length() + args[1].length() + 2);
                        Main.parser.get(gid).parse(expr);
                        if (Main.parser.get(gid).variables.keySet().contains(args[1])) {
                            Main.parser.get(gid).variables.replace(args[1], expr);
                        } else {
                            Main.parser.get(gid).variables.put(args[1], expr);
                        }
                        send("Variable set");
                    } catch (ParserException e) {
                        send("**ERROR**: " + e.getMessage());
                    }
                }
                break;
            }
            case "get": {
                if (args.length < 2) defaultRun();
                else if (Main.parser.get(gid).variables.containsKey(args[1]))
                    send(args[1] + " = " + Main.parser.get(gid).variables.get(args[1]));
                else send("Variable not set");
                break;
            }
            default:
                defaultRun();
                break;
        }
    }
}
