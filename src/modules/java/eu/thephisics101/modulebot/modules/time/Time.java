package eu.thephisics101.modulebot.modules.time;

import eu.thephisics101.modulebot.hosts.Command;
import net.dv8tion.jda.core.entities.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Time extends Command {
    @Override
    public String getName() {
        return "time";
    }

    @Override
    public String getHelp() {
        return "Gets current time for any place in the world (almost)";
    }

    @Override
    public String[] getUsages() {
        return new String[]{""};
    }

    @Override
    public void run(Message m) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, yyy, HH:mm:ss z");
        String arg = getArg(m);
        if (arg.length() == 0) format.setTimeZone(TimeZone.getTimeZone("GMT"));
        else format.setTimeZone(TimeZone.getTimeZone(arg));
        send(format.format(new Date()));
    }
}
