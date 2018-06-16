package modulebot.calc.parser.exceptions;

import java.util.List;

public class ParserMathsException extends ParserException {
    public ParserMathsException(List<String> list) {
        super("Unknown maths: \"" + list.toString() + "\"");
    }

}
