package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.proxy.ProxyConfig;
import org.springframework.beans.factory.BeanNameAware;

/**
 * Extends {@link ProxyDataSource} to use the Spring bean name (ID) as the data source name when it is not set.
 *
 * @author Tadaya Tsuyukubo
 */
public class BeanNameProxyDataSource extends ProxyDataSource implements BeanNameAware {

    public void setBeanName(String name) {
        final String dataSourceName = getProxyConfig().getDataSourceName();
        if (dataSourceName == null || "".equals(dataSourceName)) {
            ProxyConfig proxyConfig = ProxyConfig.Builder.from(getProxyConfig()).dataSourceName(name).build();
            setProxyConfig(proxyConfig);
        }
    }
}
