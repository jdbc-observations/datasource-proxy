package net.ttddyy.dsproxy.proxy;

import java.sql.ResultSet;

/**
 * Factory to create {@link ResultSetProxyLogic}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public interface ResultSetProxyLogicFactory {

    ResultSetProxyLogicFactory DEFAULT = new SimpleResultSetProxyLogicFactory();

    ResultSetProxyLogic create(ResultSet resultSet, ProxyConfig proxyConfig);
}
