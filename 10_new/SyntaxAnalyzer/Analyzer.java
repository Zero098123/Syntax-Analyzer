Asliddin, [06.12.2024 21:49]
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class SyntaxAnalyzer {

    // Token patterns for Jack language
    private static final String KEYWORD = "\b(class|constructor|function|method|field|static|var|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return)\b";
    private static final String SYMBOL = "[{}()\[\].,;+\-*/&|<>=~]";
    private static final String INTEGER_CONSTANT = "\b\d+\b";
    private static final String STRING_CONSTANT = "\"[^\"\n]*\"";
    private static final String IDENTIFIER = "\b[A-Za-z_]\w*\b";

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            String.format("(?<KEYWORD>%s)|(?<SYMBOL>%s)|(?<INTEGER_CONSTANT>%s)|(?<STRING_CONSTANT>%s)|(?<IDENTIFIER>%s)",
                    KEYWORD, SYMBOL, INTEGER_CONSTANT, STRING_CONSTANT, IDENTIFIER)
    );

    private static class Token {
        String type;
        String value;

        Token(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    private static class Tokenizer {
        private final List<Token> tokens = new ArrayList<>();
        private int current = 0;

        Tokenizer(String code) {
            Matcher matcher = TOKEN_PATTERN.matcher(code);
            while (matcher.find()) {
                if (matcher.group("KEYWORD") != null) {
                    tokens.add(new Token("keyword", matcher.group("KEYWORD")));
                } else if (matcher.group("SYMBOL") != null) {
                    tokens.add(new Token("symbol", matcher.group("SYMBOL")));
                } else if (matcher.group("INTEGER_CONSTANT") != null) {
                    tokens.add(new Token("integerConstant", matcher.group("INTEGER_CONSTANT")));
                } else if (matcher.group("STRING_CONSTANT") != null) {
                    tokens.add(new Token("stringConstant", matcher.group("STRING_CONSTANT")));
                } else if (matcher.group("IDENTIFIER") != null) {
                    tokens.add(new Token("identifier", matcher.group("IDENTIFIER")));
                }
            }
        }

        boolean hasMoreTokens() {
            return current < tokens.size();
        }

        Token peek() {
            return hasMoreTokens() ? tokens.get(current) : null;
        }

        Token advance() {
            return hasMoreTokens() ? tokens.get(current++) : null;
        }

        Token consume(String expectedType, String expectedValue) {
            Token token = peek();
            if (token == null  !token.type.equals(expectedType)  (expectedValue != null && !token.value.equals(expectedValue))) {
                throw new IllegalArgumentException("Expected " + expectedType + " with value " + expectedValue);
            }
            return advance();
        }
    }

    private static class Parser {
        private final Tokenizer tokenizer;
        private final StringBuilder output = new StringBuilder();

        Parser(Tokenizer tokenizer) {
            this.tokenizer = tokenizer;
        }

        String parse() {
            output.append("<class>\n");
            compileClass();
            output.append("</class>\n");
            return output.toString();
        }

        private void compileClass() {
            writeToken(tokenizer.consume("keyword", "class"));
            writeToken(tokenizer.consume("identifier", null));
            writeToken(tokenizer.consume("symbol", "{"));

            while (tokenizer.peek() != null && Arrays.asList("static", "field").contains(tokenizer.peek().value)) {
                compileClassVarDec();
            }

            while (tokenizer.peek() != null && Arrays.asList("constructor", "function", "method").contains(tokenizer.peek().value)) {
                compileSubroutine();
            }

            writeToken(tokenizer.consume("symbol", "}"));
        }

        Asliddin, [06.12.2024 21:49]
        private void compileClassVarDec() {
            output.append("<classVarDec>\n");
            writeToken(tokenizer.consume("keyword", null)); // static or field
            writeToken(tokenizer.consume("identifier", null)); // type
            writeToken(tokenizer.consume("identifier", null)); // varName

            while (tokenizer.peek() != null && ",".equals(tokenizer.peek().value)) {
                writeToken(tokenizer.consume("symbol", ","));
                writeToken(tokenizer.consume("identifier", null));
            }

            writeToken(tokenizer.consume("symbol", ";"));
            output.append("</classVarDec>\n");
        }

        private void compileSubroutine() {
            output.append("<subroutineDec>\n");
            writeToken(tokenizer.consume("keyword", null)); // constructor, function, or method
            writeToken(tokenizer.consume("keyword", null)); // void or type
            writeToken(tokenizer.consume("identifier", null)); // subroutineName
            writeToken(tokenizer.consume("symbol", "("));
            compileParameterList();
            writeToken(tokenizer.consume("symbol", ")"));
            compileSubroutineBody();
            output.append("</subroutineDec>\n");
        }

        private void compileParameterList() {
            output.append("<parameterList>\n");
            while (tokenizer.peek() != null && !")".equals(tokenizer.peek().value)) {
                writeToken(tokenizer.advance());
            }
            output.append("</parameterList>\n");
        }

        private void compileSubroutineBody() {
            output.append("<subroutineBody>\n");
            writeToken(tokenizer.consume("symbol", "{"));
            // Handle variable declarations and statements here
            writeToken(tokenizer.consume("symbol", "}"));
            output.append("</subroutineBody>\n");
        }

        private void writeToken(Token token) {
            String value = token.value;
            if ("<".equals(value)) value = "&lt;";
            if (">".equals(value)) value = "&gt;";
            if ("&".equals(value)) value = "&amp;";
            output.append(String.format("<%s> %s </%s>\n", token.type, value, token.type));
        }
    }

    public static void main(String[] args) throws IOException {
        String inputFilePath = "Main.jack";
        String outputFilePath = "Main.xml";

        String code = new String(Files.readAllBytes(new File(inputFilePath).toPath()));
        Tokenizer tokenizer = new Tokenizer(code);
        Parser parser = new Parser(tokenizer);

        String xmlOutput = parser.parse();
        Files.write(new File(outputFilePath).toPath(), xmlOutput.getBytes());
    }
}