package eu.thephisics101.modulebot.modules.latex;

import eu.thephisics101.modulebot.hosts.Command;
import eu.thephisics101.modulebot.hosts.CommandHost;

public class Main extends CommandHost {
//    private static final String inlineSyntax = "%%";

    @Override
    public Command[] getCommands() {
        return new Command[] { new Latex(), new Tex() };
    }

    @Override
    public String getName() {
        return "latex";
    }

    @Override
    public String getDescription() {// TODO: inline support
        return "Provides easy LaTeX image generation";//, supports inline as well (anything between " + inlineSyntax + " will be treated as latex)";
    }
}
