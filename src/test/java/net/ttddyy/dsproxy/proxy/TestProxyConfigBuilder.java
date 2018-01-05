package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.TestUtils;
import net.ttddyy.dsproxy.proxy.delegate.DelegatingJdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;

/**
 * Create ProxyConfig aware with proxy type.
 * TODO: document
 *
 * @author Tadaya Tsuyukubo
 */
public class TestProxyConfigBuilder extends ProxyConfig.Builder {

    private boolean isProxyFactorySpecified;

    public static ProxyConfig.Builder create() {
        return new TestProxyConfigBuilder();
    }

    @Override
    public ProxyConfig build() {

        // if proxy-factory is specified explicitly, use it.
        // otherwise, depends on the system prop
        if (!this.isProxyFactorySpecified) {
            if (TestUtils.isTestingProxy()) {
                this.jdbcProxyFactory(new JdkJdbcProxyFactory());  // use default
            } else {
                this.jdbcProxyFactory(new DelegatingJdbcProxyFactory());
            }
        }

        return super.build();
    }

    @Override
    public ProxyConfig.Builder jdbcProxyFactory(JdbcProxyFactory jdbcProxyFactory) {
        this.isProxyFactorySpecified = true;
        return super.jdbcProxyFactory(jdbcProxyFactory);
    }
}
