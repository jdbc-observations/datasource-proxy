package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.proxy.DefaultConnectionIdManager;

import java.sql.Connection;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.2
 */
public interface ConnectionIdManager {
    ConnectionIdManager DEFAULT = new DefaultConnectionIdManager();

    long getId(Connection connection);
}
