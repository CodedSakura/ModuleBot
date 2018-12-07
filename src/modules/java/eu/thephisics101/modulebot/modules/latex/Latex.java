package eu.thephisics101.modulebot.modules.latex;

import eu.thephisics101.modulebot.hosts.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.net.HttpURLConnection;
import java.net.URL;

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
    public void run(Message m) throws Exception {
        String latexLink = "https://chart.apis.google.com/chart?cht=tx&chf=bg,s,32363C&chco=FFFFFF&chs=40&chl=";
        URL u = new URL(latexLink + getArg(m).replaceAll("\\\\bbR", "\\\\mathbb{R}").replaceAll("\\\\bbC", "\\\\mathbb{C}"));
        HttpURLConnection huc = (HttpURLConnection) u.openConnection();
        huc.setRequestMethod("GET");
        huc.connect();
        EmbedBuilder eb = new EmbedBuilder();
        if (huc.getResponseCode() == HttpURLConnection.HTTP_OK) eb.setImage(u.toString());
        else eb.setImage("https://chart.apis.google.com/chart?cht=tx&chf=bg,s,32363C&chco=FFFFFF&chs=40&chl=\\text{Malformed\\qquad}\\LaTeX{}");
        send(eb.build());
    }
}
