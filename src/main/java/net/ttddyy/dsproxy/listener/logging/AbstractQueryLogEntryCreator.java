package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

import java.sql.Connection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public abstract class AbstractQueryLogEntryCreator implements QueryLogEntryCreator {

    protected static final Map<Character, String> JSON_SPECIAL_CHARS = new HashMap<Character, String>();

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

    protected ParameterValueConverter setNullParameterValueConverter = new SetNullParameterValueConverter();
    protected ParameterValueConverter registerOutParameterValueConverter = new RegisterOutParameterValueConverter();

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
                return (leftInt < rightInt) ? -1 : ((leftInt == rightInt) ? 0 : 1);
            } catch (NumberFormatException e) {
                return left.compareTo(right);  // use String comparison
            }
        }
    }

    protected void chompIfEndWith(StringBuilder sb, char c) {
        final int lastCharIndex = sb.length() - 1;
        if (sb.charAt(lastCharIndex) == c) {
            sb.deleteCharAt(lastCharIndex);
        }
    }

    protected String getTransactionIsolation(int isolationLevel) {
        switch (isolationLevel) {
            case Connection.TRANSACTION_NONE:
                return "NONE";
            case Connection.TRANSACTION_READ_UNCOMMITTED:
                return "READ_UNCOMMITTED";
            case Connection.TRANSACTION_READ_COMMITTED:
                return "READ_COMMITTED";
            case Connection.TRANSACTION_REPEATABLE_READ:
                return "REPEATABLE_READ";
            case Connection.TRANSACTION_SERIALIZABLE:
                return "SERIALIZABLE";
        }
        return "";
    }

    protected String getStatementType(StatementType statementType) {
        if (StatementType.STATEMENT.equals(statementType)) {
            return "Statement";
        } else if (StatementType.PREPARED.equals(statementType)) {
            return "Prepared";
        } else if (StatementType.CALLABLE.equals(statementType)) {
            return "Callable";
        }
        return "Unknown";
    }

    /**
     * populate param map with sorted by key.
     *
     * @param params list of ParameterSetOperation
     * @return a map: key=index/name as string,  value=first value
     * @since 1.4
     */
    protected SortedMap<String, String> getParametersToDisplay(List<ParameterSetOperation> params) {
        // populate param map with sorted by key: key=index/name, value=first value
        SortedMap<String, String> paramMap = new TreeMap<String, String>(new StringAsIntegerComparator());
        for (ParameterSetOperation param : params) {
            String key = getParameterKeyToDisplay(param);
            String value = getParameterValueToDisplay(param);
            paramMap.put(key, value);
        }
        return paramMap;
    }

    /**
     * @param param parameter set operation
     * @return parameterIndex or parameterName as String
     * @since 1.4
     */
    public String getParameterKeyToDisplay(ParameterSetOperation param) {
        Object key = param.getArgs()[0];  // either int(parameterIndex) or string(parameterName)
        return key instanceof String ? (String) key : key.toString();
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


    /**
     * @param param parameter set operation
     * @return value to display
     * @since 1.4
     */
    public String getDisplayValueForSetNull(ParameterSetOperation param) {
        return this.setNullParameterValueConverter.getValue(param);
    }

    /**
     * @param param parameter set operation
     * @return value to display
     * @since 1.4
     */
    public String getDisplayValueForRegisterOutParameter(ParameterSetOperation param) {
        return this.registerOutParameterValueConverter.getValue(param);
    }

    /**
     * @param param parameter set operation
     * @return value to display
     * @since 1.4
     */
    public String getDisplayValue(ParameterSetOperation param) {
        Object value = param.getArgs()[1];
        return value == null ? null : value.toString();
    }

    /**
     * @param setNullParameterValueConverter parameter value converter
     * @since 1.4
     */
    public void setSetNullParameterValueConverter(ParameterValueConverter setNullParameterValueConverter) {
        this.setNullParameterValueConverter = setNullParameterValueConverter;
    }

    /**
     * @param registerOutParameterValueConverter parameter value converter
     * @since 1.4
     */
    public void setRegisterOutParameterValueConverter(ParameterValueConverter registerOutParameterValueConverter) {
        this.registerOutParameterValueConverter = registerOutParameterValueConverter;
    }

}
