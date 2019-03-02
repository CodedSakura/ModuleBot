package eu.thephisics101.modulebot.modules.calc.parser.exceptions;

public class ParserNumberException extends ParserException {
    public ParserNumberException(String number) {
        super("Invalid number \"" + number + "\"");
    }
}
