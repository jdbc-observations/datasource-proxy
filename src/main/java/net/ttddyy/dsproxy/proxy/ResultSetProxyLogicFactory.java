package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.sql.ResultSet;
import java.util.List;

/**
 * Factory to create {@link ResultSetProxyLogic}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public interface ResultSetProxyLogicFactory {

    ResultSetProxyLogicFactory DEFAULT = new SimpleResultSetProxyLogicFactory();

    ResultSetProxyLogic create(ResultSet resultSet, List<QueryInfo> queries, ConnectionInfo connectionInfo, ProxyConfig proxyConfig);
}
