package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.ProxyTestDataSource;

/**
 * Aggregated assertj assertions.
 *
 * @author Tadaya Tsuyukubo
 * @see ProxyTestDataSourceAssert
 * @since 1.4
 */
public class DataSourceProxyAssertions {

    public static ProxyTestDataSourceAssert assertThat(ProxyTestDataSource actual) {
        return new ProxyTestDataSourceAssert(actual);
    }

}
