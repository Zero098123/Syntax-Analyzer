import myenum.TokenType;
import utils.NumberUtils;
import utils.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static myenum.StringEnum.*;
import static myenum.StringEnum.KEYWORD_FIELD;
import static myenum.StringEnum.KEYWORD_STATIC;
import static myenum.TokenType.*;

public class JackTokenizer {

    private List<String> tokens;
    private int pointer;
    private String thisToken;
    private String fileName;
    private String filePath;
    private final String BLANK = " ";

    public JackTokenizer(String filePath) {
        initPointer();
        tokens = new ArrayList();
        String line;
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            this.filePath = filePath;
            File file = new File(filePath);
            String tempFileName = file.getName();
            this.fileName = tempFileName.substring(0, tempFileName.lastIndexOf('.'));
            line = in.readLine();

            // variable to check if the line is a multi-line comment
            boolean isMuilLineNeglect = false;
            while (line != null) {
                line = line.trim();

                // check if the line is a multi-line comment
                if (line.startsWith("/*") && !line.endsWith("*/")) {
                    isMuilLineNeglect = true;
                    line = in.readLine();
                    continue;
                } else if (line.endsWith("*/") || line.startsWith("*/")) {
                    isMuilLineNeglect = false;
                    line = in.readLine();
                    continue;
                } else if (line.startsWith("/*") && line.endsWith("*/")) {
                    line = in.readLine();
                    continue;
                }

                // neglect empty lines and single line comments
                if (line.equals("") || isMuilLineNeglect || line.startsWith("//")) {
                    line = in.readLine();
                    continue;
                }

                // e.g. let x="hello world";
                // split the line into ["let x=", "hello world"], and only tokenize the first part, and add the second part as string constant
                String[] segment = line.split("//")[0].trim().split("\"");
                boolean even = true;
                for (int i = 0; i < segment.length; i++) {
                    String statement = segment[i];
                    if (even) {
                        String[] words = statement.split("\\s+");
                        for (int j = 0; j < words.length; j++) {
                            List<String> thisLineTokes = new ArrayList<>();
                            splitToToken(words[j], thisLineTokes);
                            tokens.addAll(thisLineTokes);
                        }
                        even = false;
                    } else {
                        tokens.add(StringUtils.wrapByDoubleQuotation(statement));
                        even = true;
                    }
                }
                line = in.readLine();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Splits a given word into tokens and adds them to the provided list.
     * This method handles symbols and recursively splits the word if it contains any symbols.
     *
     * @param word   the word to be split into tokens
     * @param tokens the list to which the tokens will be added
     */
    private void splitToToken(String word, List<String> tokens) {
        if (word == null || word.isEmpty()) {
            return;
        }
        if (word.length() == 1) {
            tokens.add(word);
            return;
        }
        boolean isContainSymbol = false;

        for (int i = 0; i < symbols.size(); i++) {
            String symbol = symbols.get(i);
            if (word.contains(symbol)) {
                isContainSymbol = true;
                int symbolIdx = word.indexOf(symbol);
                splitToToken(word.substring(0, symbolIdx), tokens);
                tokens.add(symbol);
                if (symbolIdx + 1 < word.length()) {
                    splitToToken(word.substring(symbolIdx + 1), tokens);
                }
                break;
            }
        }
        if (!isContainSymbol) {
            tokens.add(word);
        }
    }

    public void advance() {
        pointer++;
        this.thisToken = tokens.get(pointer);
    }


    public Boolean hasMoreTokens() {
        return pointer < tokens.size() - 1;
    }


    public TokenType tokenType() {
        // TODO: Implement token type determination logic
        // Hints:
        // - Check if `thisToken` is in the `keywords` list and return `KEYWORD` if true.
        // - Check if `thisToken` is in the `symbols` list and return `SYMBOL` if true.
        // - Use `NumberUtils.isNumeric(thisToken)` to check for numeric values and return `INT_CONSTANT` if true.
        // - Check if `thisToken` starts and ends with double quotes to identify string constants and return `STRING_CONSTANT` if true.
        // - If `thisToken` starts with a digit, throw a `RuntimeException` for syntax error.
        // - If none of the above conditions are met, return `IDENTIFIER`.
        return null;
    }

    public String keyword() {
        if (tokenType() != KEYWORD) {
            throw new RuntimeException("only when type of token is 'KEYWORD' can keyword()");
        }
        return thisToken;
    }

    public String symbol() {
        if (tokenType() != SYMBOL) {
            throw new RuntimeException("only when type of token is 'SYMBOL' can symbol()");
        }
        String token = thisToken;
        switch (thisToken) {
            case ">":
                token = "&gt;";
                break;
            case "<":
                token = "&lt;";
                break;
            case "&":
                token = "&amp;";
                break;
        }
        return token;
    }

    public String identifier() {
        if (tokenType() != IDENTIFIER) {
            throw new RuntimeException("only when type of token is 'IDENTIFIER' can identifier()");
        }
        return thisToken;
    }

    public int intVal() {
        if (tokenType() != INT_CONSTANT) {
            throw new RuntimeException("only when type of token is 'INT_CONSTANT' can intVal()");
        }
        return Integer.parseInt(thisToken);
    }

    public String stringVal() {
        if (tokenType() != STRING_CONSTANT) {
            throw new RuntimeException("only when type of token is 'STRING_CONSTANT' can stringVal()");
        }
        return thisToken.replace("\"", "");
    }

    public void initPointer() {
        pointer = -1;
    }

    public String getThisToken() {
        return switch (tokenType()) {
            case SYMBOL -> symbol();
            case KEYWORD -> keyword();
            case IDENTIFIER -> identifier();
            case INT_CONSTANT -> String.valueOf(intVal());
            case STRING_CONSTANT -> stringVal();
        };
    }

    public String getThisTokenAsTag() {
        return switch (tokenType()) {
            case SYMBOL -> StringUtils.wrapBySymbolTag(symbol());
            case KEYWORD -> StringUtils.wrapByKeywordTag(keyword());
            case IDENTIFIER -> StringUtils.wrapByIdentifierTag(identifier());
            case INT_CONSTANT -> StringUtils.wrapByIntegerConstantTag(String.valueOf(intVal()));
            case STRING_CONSTANT -> StringUtils.wrapByStringConstantTag(stringVal());
        };
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isKeywordConstant() {
        if (thisToken.equals(KEYWORD_TRUE) ||
                thisToken.equals(KEYWORD_FALSE) ||
                thisToken.equals(KEYWORD_NULL) ||
                thisToken.equals(KEYWORD_THIS)) {
            return true;
        }
        return false;
    }


    public boolean isFunKeyword() {
        if (thisToken.equals(KEYWORD_CONSTRUCTOR) ||
                thisToken.equals(KEYWORD_FUNCTION) ||
                thisToken.equals(KEYWORD_METHOD)) {
            return true;
        }
        return false;
    }

    public boolean isPrimitiveType() {
        if (thisToken.equals(KEYWORD_INT) ||
                thisToken.equals(KEYWORD_CHAR) ||
                thisToken.equals(KEYWORD_BOOLEAN)) {
            return true;
        }
        return false;
    }

    public boolean isStatement() {
        if (tokenType() == TokenType.KEYWORD &&
                (thisToken.equals(KEYWORD_LET) ||
                        thisToken.equals(KEYWORD_IF) ||
                        thisToken.equals(KEYWORD_WHILE) ||
                        thisToken.equals(KEYWORD_DO) ||
                        thisToken.equals(KEYWORD_RETURN))) {
            return true;
        }
        return false;
    }

    public boolean isClassVarType() {
        if (thisToken.equals(KEYWORD_FIELD) ||
                thisToken.equals(KEYWORD_STATIC)) {
            return true;
        }
        return false;
    }

    public boolean isVarType() {
        if (thisToken.equals(KEYWORD_VAR)) {
            return true;
        }
        return false;
    }

    public boolean isOp() {
        if (tokenType() == TokenType.SYMBOL) {
            switch (thisToken) {
                case "+":
                case "-":
                case "*":
                case "/":
                case "&":
                case "|":
                case "<":
                case ">":
                case "=":
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    public boolean isUnaryOp() {
        if (tokenType() == TokenType.SYMBOL &&
                (thisToken.equals("-") || thisToken.equals("~"))) {
            return true;
        }
        return false;
    }
}
