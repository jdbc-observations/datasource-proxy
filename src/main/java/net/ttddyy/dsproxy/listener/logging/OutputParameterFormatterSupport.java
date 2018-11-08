package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * In addition to {@link ExecutionInfoFormatter}, append output parameter values to the log for {@link CallableStatement}.
 *
 * @author Tadaya Tsuyukubo
 * @author Parikshit Navgire (navgire@optymyze.com)
 * @see ExecutionInfoFormatter
 * @since 2.0
 */
public class OutputParameterFormatterSupport extends AbstractFormatterSupport<ExecutionInfo> {

    public static BiConsumer<ExecutionInfo, StringBuilder> onOutputParameter = (execInfo, sb) -> {
        sb.append("OutParams:[");

        for (QueryInfo queryInfo : execInfo.getQueries()) {
            for (List<ParameterSetOperation> parameters : queryInfo.getParametersList()) {
                sb.append("(");

                parameters.stream()
                        .filter(ParameterSetOperation::isRegisterOutParameterOperation)
                        .forEach(parameterSetOperation -> {
                            CallableStatement cs = (CallableStatement) execInfo.getStatement();
                            Object key = parameterSetOperation.getArgs()[0];
                            Object value = getOutputValueForDisplay(key, cs);

                            sb.append(key);
                            sb.append("=");
                            sb.append(value);
                            sb.append(",");
                        });
                chompIfEndWith(sb, ',');
                sb.append("),");
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
