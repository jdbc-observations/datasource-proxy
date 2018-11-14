package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import net.ttddyy.dsproxy.proxy.ParameterSetOperations;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.function.BiConsumer;

/**
 * In addition to {@link QueryExecutionContextJsonFormatter}, append output parameter values to the log for {@link CallableStatement}.
 *
 * @author Tadaya Tsuyukubo
 * @author Parikshit Navgire (navgire@optymyze.com)
 * @see QueryExecutionContextFormatter
 * @since 2.0
 */
public class OutputParameterJsonFormatterSupport extends AbstractFormatterSupport<QueryExecutionContext> {

    public static BiConsumer<QueryExecutionContext, StringBuilder> onOutputParameter = (queryContext, sb) -> {

        sb.append("\"outParams\":[");

        for (QueryInfo queryInfo : queryContext.getQueries()) {
            for (ParameterSetOperations parameterSetOperations : queryInfo.getParameterSetOperations()) {
                sb.append("{");

                parameterSetOperations.getOperations().stream()
                        .filter(ParameterSetOperation::isRegisterOutParameterOperation)
                        .forEach(parameterSetOperation -> {
                            CallableStatement cs = (CallableStatement) queryContext.getStatement();
                            Object key = parameterSetOperation.getArgs()[0];
                            Object value = getOutputValueForDisplay(key, cs);

                            sb.append("\"");
                            sb.append(escapeSpecialCharacterForJson(key.toString()));
                            sb.append("\":");

                            if (value == null) {
                                sb.append("null");
                            } else {
                                sb.append("\"");
                                sb.append(value);
                                sb.append("\"");
                            }
                            sb.append(",");

                        });
                chompIfEndWith(sb, ',');
                sb.append("},");
            }
        }

        chompIfEndWith(sb, ',');
        sb.append("]");
    };

    private static Object getOutputValueForDisplay(Object key, CallableStatement cs) {
        Object value;
        try {
            if (key instanceof String) {
                value = cs.getObject((String) key);  // access by name
            } else {
                value = cs.getObject((Integer) key);  // access by index
            }
        } catch (SQLException e) {
            return "[FAILED TO RETRIEVE]";
        }
        return value;
    }

}
