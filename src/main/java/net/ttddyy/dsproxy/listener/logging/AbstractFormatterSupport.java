package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.function.DSProxyBiConsumer;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import net.ttddyy.dsproxy.proxy.ParameterSetOperations;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public abstract class AbstractFormatterSupport<T> {

    protected static final String DEFAULT_DELIMITER = ", ";

    // System.lineSeparator() is from java1.7
    protected static final String LINE_SEPARATOR = System.getProperty("line.separator");

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

    /**
     * Comparator considering string as integer.
     *
     * When it has null, put it as first element(smaller).
     * If string cannot be parsed to integer, it compared as string.
     */
    protected static class StringAsIntegerComparator implements Comparator<String> {
        @Override
        public int compare(String left, String right) {
            // make null first
            if (left == null && right == null) {
                return 0;
            }
            if (left == null) {
                return -1; // right is greater
            }
            if (right == null) {
                return 1; // left is greater;
            }

            try {
                int leftInt = Integer.parseInt(left);
                int rightInt = Integer.parseInt(right);
                return (leftInt < rightInt) ? -1 : ((leftInt == rightInt) ? 0 : 1);  // Integer.compare(int, int) is for java1.7
            } catch (NumberFormatException e) {
                return left.compareTo(right);  // use String comparison
            }
        }

    }

    protected DSProxyBiConsumer<T, StringBuilder> newLine = (executionContext, sb) -> {
        sb.append(LINE_SEPARATOR);
    };

    protected String delimiter = DEFAULT_DELIMITER;

    protected ParameterValueConverter parameterValueConverter = new SimpleParameterValueConverter();
    protected ParameterValueConverter setNullParameterValueConverter = new SetNullParameterValueConverter();
    protected ParameterValueConverter registerOutParameterValueConverter = new RegisterOutParameterValueConverter();

    protected SortedMap<String, String> getParametersToDisplay(ParameterSetOperations params) {

        SortedMap<String, String> sortedMap = new TreeMap<>(new StringAsIntegerComparator());
        for (ParameterSetOperation param : params.getOperations()) {
            String key = getParameterKeyToDisplay(param);
            String value = getParameterValueToDisplay(param);
            sortedMap.put(key, value);
        }
        return sortedMap;
    }

    protected String getParameterKeyToDisplay(ParameterSetOperation param) {
        return param.getParameterKey().getKeyAsString();
    }

    protected String getParameterValueToDisplay(ParameterSetOperation param) {

        String value;
        if (ParameterSetOperation.isSetNullParameterOperation(param)) {
            // for setNull
            value = getDisplayValueForSetNull(param);
        } else if (ParameterSetOperation.isRegisterOutParameterOperation(param)) {
            // for registerOutParameter
            value = getDisplayValueForRegisterOutParameter(param);
        } else {
            value = getDisplayValue(param);
        }
        return value;
    }

    protected String getDisplayValueForSetNull(ParameterSetOperation param) {
        return this.setNullParameterValueConverter.getValue(param);
    }

    protected String getDisplayValueForRegisterOutParameter(ParameterSetOperation param) {
        return this.registerOutParameterValueConverter.getValue(param);
    }

    protected String getDisplayValue(ParameterSetOperation param) {
        return this.parameterValueConverter.getValue(param);
    }

}
