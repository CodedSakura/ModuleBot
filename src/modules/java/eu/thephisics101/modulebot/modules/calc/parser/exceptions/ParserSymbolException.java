package eu.thephisics101.modulebot.modules.calc.parser.exceptions;

public class ParserSymbolException extends ParserException {
    public ParserSymbolException(String symbol) {
        super("Unexpected symbol \"" + symbol + "\"");
    }
}
