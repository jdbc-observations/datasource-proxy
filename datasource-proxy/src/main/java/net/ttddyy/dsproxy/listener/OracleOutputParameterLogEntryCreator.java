package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/**
 * @author Parikshit Navgire (navgire@optymyze.com)
 */
public class OracleOutputParameterLogEntryCreator implements QueryLogEntryCreator {

    private DefaultQueryLogEntryCreator logEntryCreator = new DefaultQueryLogEntryCreator();

    @Override
    public String getLogEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName) {
        final StringBuilder sb = new StringBuilder();
        for (QueryInfo queryInfo : queryInfoList) {
            sb.append(logEntryCreator.getLogEntry(execInfo, Arrays.asList(queryInfo), writeDataSourceName));
            if (hasOutputParameters(queryInfo)) {
                sb.append(getOutputParametersInDefaultFormat(queryInfo, execInfo.getStatement(), " = "));
            }
        }
        return sb.toString();
    }

    @Override
    public String getLogEntryAsJson(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName) {
        final StringBuilder sb = new StringBuilder();
        for (QueryInfo queryInfo : queryInfoList) {
            sb.append(logEntryCreator.getLogEntryAsJson(execInfo, Arrays.asList(queryInfo), writeDataSourceName));
            if (hasOutputParameters(queryInfo)) {
                sb.append(getOutputParametersInJsonFormat(queryInfo, execInfo.getStatement(), " : "));
            }
        }
        return sb.toString();
    }

    private String getOutputParametersInDefaultFormat(QueryInfo queryInfo, Statement st, String seperator) {
        final StringBuilder sb = new StringBuilder();
        sb.append(", Out Params:[(");
        sb.append(getOutputParameters(queryInfo, (CallableStatement) st, seperator));
        sb.append(")]");
        return sb.toString();
    }

    private String getOutputParametersInJsonFormat(QueryInfo queryInfo, Statement st, String seperator) {
        final StringBuilder sb = new StringBuilder();
        sb.append(", Out Params:[{");
        sb.append(getOutputParameters(queryInfo, (CallableStatement) st, seperator));
        sb.append("}]");
        return sb.toString();
    }

    private String getOutputParameters(QueryInfo queryInfo, CallableStatement st, String seperator) {
        final StringBuilder sb = new StringBuilder();
        try {
            for (Integer index : queryInfo.getOutParamIndexes()) {
                sb.append(index);
                sb.append(seperator);
                sb.append(st.getObject(index));
                sb.append(", ");
            }
            if (!queryInfo.getOutParamIndexes().isEmpty()) {
                sb.deleteCharAt(sb.length() - 1);
            }
            for (String paramName : queryInfo.getOutParamNames()) {
                sb.append(paramName);
                sb.append(seperator);
                sb.append(st.getObject(paramName));
                sb.append(", ");
            }
            if (!queryInfo.getOutParamNames().isEmpty()) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private boolean hasOutputParameters(QueryInfo queryInfo) {
        return !queryInfo.getOutParamNames().isEmpty() || !queryInfo.getOutParamIndexes().isEmpty();
    }

}
