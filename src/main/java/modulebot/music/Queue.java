package modulebot.music;

public class Queue extends List {
    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getHelp() {
        return "Alias of list";
    }
}
