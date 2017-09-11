package net.ttddyy.dsproxy.proxy;

import java.sql.ResultSet;

/**
 * Factory to create {@link SimpleResultSetProxyLogic}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class SimpleResultSetProxyLogicFactory implements ResultSetProxyLogicFactory {

    @Override
    public ResultSetProxyLogic create(ResultSet resultSet, ProxyConfig proxyConfig) {
        return new SimpleResultSetProxyLogic(resultSet, proxyConfig);
    }

}
