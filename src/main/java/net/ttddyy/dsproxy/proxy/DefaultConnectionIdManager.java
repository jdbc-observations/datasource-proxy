package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionIdManager;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default implementation of {@link ConnectionIdManager}.
 *
 * This implementation returns sequentially increasing unique number as connection id.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.2
 */
public class DefaultConnectionIdManager implements ConnectionIdManager {

    private AtomicLong idCounter = new AtomicLong(0);

    @Override
    public long getId(Connection connection) {
        return this.idCounter.incrementAndGet();
    }
}
