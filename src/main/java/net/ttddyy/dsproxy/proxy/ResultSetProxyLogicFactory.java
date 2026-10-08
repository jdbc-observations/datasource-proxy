package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;

import java.sql.ResultSet;

/**
 * Factory for creating {@link ResultSetProxyLogic}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public interface ResultSetProxyLogicFactory {

    ResultSetProxyLogicFactory DEFAULT = new SimpleResultSetProxyLogicFactory();

    ResultSetProxyLogic create(ResultSet resultSet, ConnectionInfo connectionInfo, ProxyConfig proxyConfig);
}
