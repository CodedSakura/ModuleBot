package modulebot.calc.parser;

import modulebot.calc.parser.exceptions.ParserMathsException;
import modulebot.calc.parser.exceptions.ParserNumberException;
import modulebot.calc.parser.exceptions.ParserSymbolException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

import static java.lang.Math.E;
import static java.lang.Math.PI;

public class Parser {
    private final static String DOF = "-?[\\d.]+E?[-+]?\\d+|-?\\d"; // definition of a floating point number with exponent in ReGeX
    private final static String DOI = "-?[\\d]+E?[-+]?\\d+|-?\\d"; // definition of an integer with exponent in ReGeX
    private final int SCALE;
    private final MathContext mc;
    public Map<String, String> variables = new HashMap<>();

    public Parser() {
        SCALE = 32  ;
        mc = new MathContext(SCALE, RoundingMode.HALF_EVEN);
    }
    public Parser(int scale) {
        SCALE = Math.abs(scale);
        mc = new MathContext(SCALE, RoundingMode.HALF_EVEN);
    }

    public String parse(String input) {
        List<String> data = new ArrayList<>(Arrays.asList(input.split("")));
        if (data.get(0).equals("-") && data.get(1).matches("[\\d.]")) data.set(0, "-0");

        if (data.get(0).equals("")) return null;


        // Interpreter

        for (int i = 1; i < data.size(); i++) {
            int j = i - 1;
            if (data.get(j).matches("-?[\\d.]+E?[-+]?\\d*") && data.get(i).matches("\\d")) {
                data.set(j, data.get(j) + data.get(i));
                data.remove(i);
                i--;
            } else if (data.get(i).equals(".")) {
                if (data.get(j).matches("-?\\d+")) {
                    data.set(j, data.get(j) + data.get(i));
                    data.remove(i);
                    i--;
                } else {
                    throw new ParserSymbolException(".");
                }
            } else if (data.get(i).equals("E")) {
                if (data.get(j).matches("-?[\\d.]+")) {
                    data.set(j, data.get(j) + "E");
                    data.remove(i);
                    i--;
                } else if (data.get(j).matches("-?[\\d.E]+")) {
                    throw new ParserSymbolException("E");
                }
            } else if (data.get(i).equals("-")) {
                if (!data.get(j).matches("-?[\\d.E]+|\\)")) {
                    data.set(i, "-0");
                } else if (data.get(j).matches("-?[\\d.]+E")) {
                    data.set(j, data.get(j) + "-");
                    data.remove(i);
                    i--;
                }
            } else if (data.get(i).equals("+")) {
                if (data.get(j).matches("-?[\\d.]+E")) {
                    data.set(j, data.get(j) + "+");
                    data.remove(i);
                    i--;
                }
            } else if (data.get(j).equals("*") && data.get(i).equals("*")) {
                data.set(j, "^");
                data.remove(i);
                i--;
            } else if (data.get(j).equals("<") && data.get(i).equals("<")) {
                data.set(j, "<<");
                data.remove(i);
                i--;
            } else if (data.get(j).equals(">") && data.get(i).equals(">")) {
                data.set(j, ">>");
                data.remove(i);
                i--;
            } else if (data.get(j).equals("=") && data.get(i).equals("=")) {
                data.set(j, "==");
                data.remove(i);
                i--;
            } else if (data.get(j).matches("[a-zA-Z]+") && data.get(i).matches("[a-zA-Z]")) {
                data.set(j, data.get(j).toLowerCase() + data.get(i).toLowerCase());
                data.remove(i);
                i--;
            }
        }

        while (data.contains(" "))  data.remove(" ");
        while (data.contains("\n")) data.remove("\n");
        while (data.contains("\t")) data.remove("\t");


        // Check
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).matches(DOF)) {
                data.set(i, new BigDecimal(data.get(i)).toString());
            } else if (data.get(i).matches("-?[\\d.]*E?-?[\\d]*") && !data.get(i).equals("-")) {
                throw new ParserNumberException(data.get(i));
            }
        }

        if (Collections.frequency(data, "(") != Collections.frequency(data, ")")) {
            throw new ParserSymbolException(Collections.frequency(data, "(") > Collections.frequency(data, ")") ? "(" : ")");
        }


        // parser
        while (data.contains("(")) {
            int end = data.indexOf(")");
            int start = data.subList(0, end).lastIndexOf("(");
            List<String> subData = new ArrayList<>(data.subList(start + 1, end));
            data.subList(start, end + 1).clear();
            data.add(start, solve(subData));
        }

        return new BigDecimal(solve(data)).toString();
    }

    private String solve(List<String> data) {

        //functions
        for (int i = 0; i < data.size() - 1; i++) {
            String curr = data.get(i);
            String next = data.get(i + 1);
            switch (curr) {
                case "abs": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.abs(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "acos": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.acos(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "asin": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.asin(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "atan": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.atan(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "cbrt": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.cbrt(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "ceil": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    data.set(i, new BigDecimal(next, mc).round(new MathContext(SCALE, RoundingMode.CEILING)).toString());
                    data.remove(i + 1);
                    break;
                }
                case "cos": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.cos(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "cosh": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.cosh(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "exp": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.exp(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "expm1": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.expm1(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "floor": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    data.set(i, new BigDecimal(next, mc).round(new MathContext(SCALE, RoundingMode.FLOOR)).toString());
                    data.remove(i + 1);
                    break;
                }
                case "log": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.log(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "log10": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.log10(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "log1p": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.log1p(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "round": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    data.set(i, new BigDecimal(next, mc).round(new MathContext(SCALE, RoundingMode.HALF_DOWN)).toString());
                    data.remove(i + 1);
                    break;
                }
                case "signum": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.signum(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "sin": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.sin(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "sinh": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.sinh(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "sqrt": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.sqrt(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "tan": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.tan(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "tanh": {
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.tanh(new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    break;
                }
                case "unset": {
                    if (!next.matches("[a-z]+")) throw new ParserSymbolException(next);
                    if (!variables.keySet().contains(next)) throw new ParserSymbolException(next);
                    data.set(i, variables.get(next));
                    data.remove(i + 1);
                    variables.remove(next);
                    break;
                }
            }
        }

        //constants
        for (int i = 0; i < data.size(); i++) {
            if (!data.get(i).matches("[a-z]+")) continue;
            if (data.get(i).equals("pi")) data.set(i, new BigDecimal(PI).toString());
            else if (data.get(i).equals("e")) data.set(i, new BigDecimal(E).toString());
            else if (data.get(i).equals("rand")) data.set(i, new BigDecimal(Math.random()).toString());
            else if (variables.keySet().contains(data.get(i))) {
                data.set(i, new BigDecimal(this.parse(variables.get(data.get(i)))).toString());
            }
        }

        //powers
        if (data.contains("^")) {
            for (int i = 1; i < data.size() - 1; i++) {
                String prev = data.get(i - 1);
                String curr = data.get(i);
                String next = data.get(i + 1);
                if (curr.equals("^")) {
                    if (!prev.matches(DOF)) throw new ParserSymbolException(prev);
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    double out = Math.pow(new BigDecimal(prev, mc).doubleValue(), new BigDecimal(next, mc).doubleValue());
                    data.set(i, BigDecimal.valueOf(out).toString());
                    data.remove(i + 1);
                    data.remove(i - 1);
                    i--;
                }
            }
            if (data.size() == 1) return data.get(0);
        }

        //multiplication, division
        if (data.contains("*") || data.contains("/") || data.contains("%")) {
            for (int i = 1; i < data.size() - 1; i++) {
                String prev = data.get(i - 1);
                String curr = data.get(i);
                String next = data.get(i + 1);
                switch (curr) {
                    case "*": {
                        if (!prev.matches(DOF)) throw new ParserSymbolException(prev);
                        if (!next.matches(DOF)) throw new ParserSymbolException(next);
                        BigDecimal out = new BigDecimal(prev, mc);
                        out = out.multiply(new BigDecimal(next, mc), mc);
                        data.set(i, out.toString());
                        data.remove(i + 1);
                        data.remove(i - 1);
                        i--;
                        break;
                    }
                    case "/": {
                        if (!prev.matches(DOF)) throw new ParserSymbolException(prev);
                        if (!next.matches(DOF)) throw new ParserSymbolException(next);
                        BigDecimal out = new BigDecimal(prev, mc);
                        out = out.divide(new BigDecimal(next, mc), mc);
                        data.set(i, out.toString());
                        data.remove(i + 1);
                        data.remove(i - 1);
                        i--;
                        break;
                    }
                    case "%": {
                        if (!prev.matches(DOF)) throw new ParserSymbolException(prev);
                        if (!next.matches(DOF)) throw new ParserSymbolException(next);
                        BigDecimal out = new BigDecimal(prev, mc);
                        out = out.remainder(new BigDecimal(next, mc), mc);
                        data.set(i, out.toString());
                        data.remove(i + 1);
                        data.remove(i - 1);
                        i--;
                        break;
                    }
                }
            }
            if (data.size() == 1) return data.get(0);
        }

        //addition, subtraction
        if (data.contains("+") || data.contains("-")) {
            for (int i = 1; i < data.size() - 1; i++) {
                String prev = data.get(i - 1);
                String curr = data.get(i);
                String next = data.get(i + 1);
                if (curr.equals("+")) {
                    if (!prev.matches(DOF)) throw new ParserSymbolException(prev);
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    BigDecimal out = new BigDecimal(prev, mc);
                    out = out.add(new BigDecimal(next, mc), mc);
                    data.set(i, out.toString());
                    data.remove(i + 1);
                    data.remove(i - 1);
                    i--;
                } else if (curr.equals("-")) {
                    if (!prev.matches(DOF)) throw new ParserSymbolException(prev);
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    BigDecimal out = new BigDecimal(prev, mc);
                    out = out.subtract(new BigDecimal(next, mc), mc);
                    data.set(i, out.toString());
                    data.remove(i + 1);
                    data.remove(i - 1);
                    i--;
                }
            }
            if (data.size() == 1) return data.get(0);
        }

        //bitShifting
        if (data.contains(">>") || data.contains("<<")) {
            for (int i = 1; i < data.size() - 1; i++) {
                String prev = data.get(i - 1);
                String curr = data.get(i);
                String next = data.get(i + 1);
                if (curr.equals(">>")) {
                    if (!prev.matches(DOI)) throw new ParserSymbolException(prev);
                    if (!next.matches(DOI)) throw new ParserSymbolException(next);
                    BigInteger out = new BigInteger(prev);
                    out = out.shiftRight(new BigInteger(next).intValue());
                    data.set(i, out.toString());
                    data.remove(i + 1);
                    data.remove(i - 1);
                    i--;
                } else if (curr.equals("<<")) {
                    if (!prev.matches(DOI)) throw new ParserSymbolException(prev);
                    if (!next.matches(DOI)) throw new ParserSymbolException(next);
                    BigInteger out = new BigInteger(prev);
                    out = out.shiftLeft(new BigInteger(next).intValue());
                    data.set(i, out.toString());
                    data.remove(i + 1);
                    data.remove(i - 1);
                    i--;
                }
            }
            if (data.size() == 1) return data.get(0);
        }

        //comparison
        if (data.contains("==")) {
            for (int i = 1; i < data.size() - 1; i++) {
                String prev = data.get(i - 1);
                String curr = data.get(i);
                String next = data.get(i + 1);
                if (curr.equals("==")) {
                    if (!prev.matches(DOF)) throw new ParserSymbolException(prev);
                    if (!next.matches(DOF)) throw new ParserSymbolException(next);
                    boolean out = new BigDecimal(prev, mc).equals(new BigDecimal(next, mc));
                    data.set(i, out ? "1" : "0");
                    data.remove(i + 1);
                    data.remove(i - 1);
                    i--;
                }
            }
            if (data.size() == 1) return data.get(0);
        }

        //variable assignment
        if (data.size() == 3 && data.get(1).equals("=")) {
            String prev = data.get(0);
            String next = data.get(2);
            if (!prev.matches("[a-z]+")) throw new ParserSymbolException(prev);
            if (!next.matches(DOF)) throw new ParserSymbolException(next);
            if (variables.keySet().contains(prev)) variables.replace(prev, next);
            else variables.put(prev, next);
            data.set(1, next);
            data.remove(2);
            data.remove(0);
        }

        if (data.size() == 1) return data.get(0);

        throw new ParserMathsException(data);
    }
}
