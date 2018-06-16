package modulebot.calc.parser.exceptions;

public class ParserSymbolException extends ParserException {
    public ParserSymbolException(String symbol) {
        super("Unexpected symbol \"" + symbol + "\"");
    }
}
