package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;

import java.sql.ResultSet;

/**
 * Factory for creating {@link SimpleResultSetProxyLogic}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class SimpleResultSetProxyLogicFactory implements ResultSetProxyLogicFactory {

    @Override
    public ResultSetProxyLogic create(ResultSet resultSet, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        return new SimpleResultSetProxyLogic(resultSet, connectionInfo, proxyConfig);
    }

}
