package net.ttddyy.dsproxy.listener.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public abstract class AbstractFormatterSupport<T> {

    protected static final String DEFAULT_DELIMITER = ", ";

    protected static final Map<Character, String> JSON_SPECIAL_CHARS = new HashMap<>();

    static {
        JSON_SPECIAL_CHARS.put('"', "\\\"");   // quotation mark
        JSON_SPECIAL_CHARS.put('\\', "\\\\");  // reverse solidus
        JSON_SPECIAL_CHARS.put('/', "\\/");    // solidus
        JSON_SPECIAL_CHARS.put('\b', "\\b");   // backspace
        JSON_SPECIAL_CHARS.put('\f', "\\f");   // formfeed
        JSON_SPECIAL_CHARS.put('\n', "\\n");   // newline
        JSON_SPECIAL_CHARS.put('\r', "\\r");   // carriage return
        JSON_SPECIAL_CHARS.put('\t', "\\t");   // horizontal tab
    }


    protected static void chompIfEndWith(StringBuilder sb, String s) {
        if (sb.length() < s.length()) {
            return;
        }
        final int startIndex = sb.length() - s.length();
        if (sb.substring(startIndex, sb.length()).equals(s)) {
            sb.delete(startIndex, sb.length());
        }
    }

    protected static void chompIfEndWith(StringBuilder sb, char c) {
        final int lastCharIndex = sb.length() - 1;
        if (sb.charAt(lastCharIndex) == c) {
            sb.deleteCharAt(lastCharIndex);
        }
    }

    protected static String escapeSpecialCharacterForJson(String input) {
        if (input == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            String value = JSON_SPECIAL_CHARS.get(c);
            sb.append(value != null ? value : c);
        }
        return sb.toString();
    }

    protected BiConsumer<T, StringBuilder> newLine = (executionContext, sb) -> {
        sb.append(System.lineSeparator());
    };

    protected String delimiter = DEFAULT_DELIMITER;

}
