package utils;

import myenum.TokenType;

public class StringUtils {
    public static String wrapByParenthesis(String str) {
        return "(" + str + ")";
    }
    public static String wrapByDoubleQuotation(String str) {
        return "\"" + str + "\"";
    }

    public static String wrapByKeywordTag(String str) {
        return wrapByTag(str, "keyword");
    }

    public static String wrapBySymbolTag(String str) {
        return wrapByTag(str, "symbol");
    }

    public static String wrapByIdentifierTag(String str) {
        return wrapByTag(str, "identifier");
    }

    public static String wrapByIntegerConstantTag(String str) {
        return wrapByTag(str, "integerConstant");
    }

    public static String wrapByStringConstantTag(String str) {
        return wrapByTag(str, "stringConstant");
    }

    public static String wrapByTag(String str, String tag) {
        return wrapBySAB(tag) + str + wrapByEAB(tag);
    }

    public static String wrapBySAB(String str) {
        return "<" + str + ">";
    }

    public static String wrapByEAB(String str) {
        return "</" + str + ">";
    }

    public static String getTokenType(TokenType tokenType) {
        switch (tokenType) {
            case KEYWORD:
                return "keyword";
            case SYMBOL:
                return "symbol";
            case IDENTIFIER:
                return "identifier";
            case INT_CONSTANT:
                return "integerConstant";
            case STRING_CONSTANT:
                return "stringConstant";
            default:
                throw new RuntimeException("unknown tokenType");
        }
    }


}
