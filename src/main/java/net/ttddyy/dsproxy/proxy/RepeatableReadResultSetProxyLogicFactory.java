package net.ttddyy.dsproxy.proxy;

import java.sql.ResultSet;
import java.util.Map;

/**
 * Factory to create {@link RepeatableReadResultSetProxyLogic}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class RepeatableReadResultSetProxyLogicFactory implements ResultSetProxyLogicFactory {

    @Override
    public ResultSetProxyLogic create(ResultSet resultSet, ProxyConfig proxyConfig) {
        Map<String, Integer> columnNameToIndex = ResultSetUtils.columnNameToIndex(resultSet);
        int columnCount = ResultSetUtils.columnCount(resultSet);
        return RepeatableReadResultSetProxyLogic.Builder.create()
                .resultSet(resultSet)
                .columnNameToIndex(columnNameToIndex)
                .columnCount(columnCount)
                .build();
    }

}
