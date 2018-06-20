package modulebot.random;

import modulebot.main.hosts.Command;
import net.dv8tion.jda.core.entities.Message;

import java.util.Random;

public class EightBall extends Command {
    @Override
    public String getName() {
        return "8ball";
    }

    @Override
    public String getHelp() {
        return "Predicts the future";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"[question]"};
    }

    @Override
    public void run(Message m) {
        Random r = new Random();
        if (!getArg(m).equals("")) r.setSeed(getArg(m).toLowerCase().hashCode());
        send(new String[] {
                "It is certain", "It is decidedly so", "Without a doubt", "Yes definitely", "You may rely on it",
                "As I see it, yes", "Most likely", "Outlook good", "Yes", "Signs point to yes", "Reply hazy try again",
                "Ask again later", "Better not tell you now", "Cannot predict now", "Concentrate and ask again",
                "Don't count on it", "My reply is no", "My sources say no", "Outlook not so good", "Very doubtful"
        }[r.nextInt(20)]);
    }
}
