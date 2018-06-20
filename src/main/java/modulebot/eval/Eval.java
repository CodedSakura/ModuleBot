package modulebot.eval;

import modulebot.main.hosts.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Eval extends Command {
    @Override
    public String getName() {
        return "eval";
    }

    @Override
    public String getHelp() {
        return "Evaluates ES5 JavaScript using Nashorn engine";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"[code]", "keys"};
    }

    @Override
    public void run(Message m) {
        if (!admin) {
            send("This command is admin only");
            return;
        }
        ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
        se.put("bot", m.getJDA());
        se.put("self", m.getAuthor());
        se.put("guild", m.getGuild());
        se.put("member", m.getMember());
        se.put("channel", m.getChannel());
        se.put("message", m);
        se.put("MessageBuilder", new MessageBuilder());
        se.put("EmbedBuilder", new EmbedBuilder());
        se.put("keys", "this eval command has 'bot', 'self', 'guild', 'member', 'channel', 'message', 'MessageBuilder', 'EmbedBuilder'");
        String text = getArg(m);
        if (text.startsWith("```")) {
            text = text.substring(3, text.length() - 3);
            if (text.startsWith("java")) text = text.substring(4);
            else if (text.startsWith("js")) text = text.substring(2);
        }
        if (text.startsWith("`")) text = text.substring(1, text.length() - 1);

        try {
            send("Evaluated successfully:```js\n" + se.eval(text) + " ```");
        } catch (Exception e) {
            send("An exception was thrown:```\n" + e + " ```");
        }
    }
}
