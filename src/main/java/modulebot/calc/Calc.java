package modulebot.calc;

import modulebot.calc.parser.Parser;
import modulebot.calc.parser.exceptions.ParserException;
import modulebot.main.hosts.Command;
import net.dv8tion.jda.core.entities.Message;

public class Calc extends Command {
    @Override
    public String getName() {
        return "calc";
    }

    @Override
    public String getHelp() {
        return "Calculates (real number) math\n(for some reason the parser dislikes if you put a space after a - sign)";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"[expression]"};
    }

    @Override
    public void run(Message m) {
        long gid = m.getGuild().getIdLong();
        if (!Main.parser.containsKey(gid)) Main.parser.put(gid, new Parser());
        try {
            send(Main.parser.get(gid).parse(getArg(m)));
        } catch (ParserException e) {
            send("**ERROR**: " + e.getMessage());
        }
    }
}
