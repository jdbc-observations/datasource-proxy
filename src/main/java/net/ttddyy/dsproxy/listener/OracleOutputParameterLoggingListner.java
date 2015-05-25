package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.support.CommonsLogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * @author Parikshit Navgire (navgire@optymyze.com)
 */
public class OracleOutputParameterLoggingListner extends  AbstractQueryLoggingListener{

    protected Log log = LogFactory.getLog(OracleOutputParameterLoggingListner.class);

    public OracleOutputParameterLoggingListner() {
        setQueryLogEntryCreator(new OracleOutputParameterLogEntryCreator());
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        final String entry = getEntry(execInfo, queryInfoList);
        writeLog(entry);
    }

    @Override
    protected void writeLog(String message) {
        CommonsLogUtils.writeLog(log, CommonsLogLevel.DEBUG, message);
    }

    protected String getEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        if (this.writeAsJson) {
            return this.queryLogEntryCreator.getLogEntryAsJson(execInfo, queryInfoList, this.writeDataSourceName);
        } else {
            return this.queryLogEntryCreator.getLogEntry(execInfo, queryInfoList, this.writeDataSourceName);
        }
    }
}
