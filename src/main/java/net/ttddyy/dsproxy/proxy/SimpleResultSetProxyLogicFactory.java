package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.sql.ResultSet;
import java.util.List;

/**
 * Factory to create {@link SimpleResultSetProxyLogic}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class SimpleResultSetProxyLogicFactory implements ResultSetProxyLogicFactory {

    @Override
    public ResultSetProxyLogic create(ResultSet resultSet, List<QueryInfo> queries, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        return new SimpleResultSetProxyLogic(resultSet, connectionInfo, proxyConfig);
    }

}
