package eu.thephisics101.modulebot.modules.convert;

import eu.thephisics101.modulebot.hosts.Command;
import net.dv8tion.jda.core.entities.Message;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigInteger;

public class Convert extends Command {
    @Override
    public String getName() {
        return "convert";
    }

    @Override
    public String getHelp() {
        return "Converts between common units";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"[from] [to] [value]"};
    }

    @Override
    public void run(Message m) throws Exception {
        String[] args = getArgs(m);
        if (args.length < 3) {
            send("missing Arguments");
            return;
        }

        String baseRegex = "b([2-9]|[1-2][0-9]|3[0-6])";
        if (args[0].matches(baseRegex) && args[1].matches(baseRegex)) {
            send(convert(new Integer(args[0].substring(1)), new Integer(args[1].substring(1)), args[2]));
        } else {
            send(convert(args[0], args[1], args[2]));
        }
    }

    private static String convert(int baseFrom, int baseTo, String in) {
        return new BigInteger(in, baseFrom).toString(baseTo).toUpperCase();
    }

    private static String convert(String from, String to, String in) throws IOException {
        Element doc  = Jsoup.connect("https://www.convertunits.com/from/" + in + "+" + from + "/to/" + to).get().body();
        Elements out = doc.select("input[onchange=backward()]");
        if (out.size() != 0) {
            return out.first().attr("value");
        } else {
            if (doc.select("i").size() != 0) {
                return "ERR: incompatible types";
            } else {
                String[] a = doc.select("font[color=red]").first().select("a").first().attr("href").split(" ");
                return convert(a[0], a[2], in);
            }
        }
    }
}
