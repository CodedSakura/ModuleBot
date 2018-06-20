package modulebot.latex;

import modulebot.main.hosts.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

public class Latex extends Command {
    @Override
    public String getName() {
        return "latex";
    }

    @Override
    public String getHelp() {
        return "Generates LaTeX image from an input string";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"[latex code]"};
    }

    @Override
    public void run(Message m) {
        String latexLink = "https://latex.codecogs.com/png.latex?\\bg_white%20\\dpi{500}&space;\\huge&space;";
        EmbedBuilder eb = new EmbedBuilder();
        eb.setImage(latexLink + getArg(m).replaceAll(" ", "&space;"));
        send(eb.build());
    }
}
