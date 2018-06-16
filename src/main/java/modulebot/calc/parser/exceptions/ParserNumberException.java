package modulebot.calc.parser.exceptions;

public class ParserNumberException extends ParserException {
    public ParserNumberException(String number) {
        super("Invalid number \"" + number + "\"");
    }
}
