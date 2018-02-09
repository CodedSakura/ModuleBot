package modulebot.main;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Command implements CMDI {
    private String id;
    private TextChannel channel;
    public boolean admin;

    public String execute(Message m, boolean admin, String id) {
        this.id = id;
        channel = m.getTextChannel();
        this.admin = admin;
        try {
            run(m);
        } catch (Exception e) {
            send("**ERROR**: command threw a " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        return this.id;
    }

    public Command setChannel(TextChannel channel) {
        this.channel = channel;
        return this;
    }

    public void defaultRun() {
        StringBuilder usages = new StringBuilder();
        for (String s : getUsages()) {
            usages.append("`").append(getName()).append(" ").append(s).append("`\n");
        }
        usages.setLength(usages.length() - 1);
        send(
                "`" + Main.prefix.get(channel.getGuild().getIdLong()) + getName() + "`\n" +
                getHelp() + "\n" +
                "usage:\n" +
                usages.toString()
        );
    }

    public void send(CharSequence m) {
        if (m.length() > 2000) m = "**ERROR**: Message is too long";
        send(new MessageBuilder().append(m).build());
    }
    public void send(MessageEmbed m) {
        send(new MessageBuilder().setEmbed(m).build());
    }
    public void send(Object m) {
        send(new MessageBuilder().append(m).build());
    }
    private void send(Message m) {
        if (id == null) id = channel.sendMessage(m).complete().getId();
        else channel.editMessageById(id, m).queue();
    }

    public Command cloneCMD() {
        Command t = this;
        return new Command() {
            @Override
            public String getName() { return t.getName(); }

            @Override
            public String getHelp() { return t.getHelp(); }

            @Override
            public String[] getUsages() { return t.getUsages(); }

            @Override
            public void run(Message m) throws Exception { t.run(m); }

            @Override
            public String execute(Message m, boolean admin, String id) { return t.execute(m, admin, id); }
        };
    }

    public String trimPrefix(Message m) {
        String c = m.getContentRaw();
        String prefix = Main.prefix.get(m.getGuild().getIdLong());
        return c.substring(
                c.startsWith(prefix) ? prefix.length() : m.getJDA().getSelfUser().getAsMention().length()
        ).trim();
    }

    public String[] getArgs(Message m) {
        ArrayList<String> args = new ArrayList<>(Arrays.asList(trimPrefix(m).toLowerCase().split(" ")));
        args.replaceAll(String::trim);
        if (args.size() < 2) return new String[0];
        if (args.get(0).equals(getName()) && !args.get(1).equals(getName())) args.remove(0);
        else {
            args.remove(0);
            args.remove(0);
        }
        if (args.size() == 0) return new String[0];
        return args.toArray(new String[0]);
    }
}
