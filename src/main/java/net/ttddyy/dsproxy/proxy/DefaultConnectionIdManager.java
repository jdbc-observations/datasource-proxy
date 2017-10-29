package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionIdManager;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
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

    private Set<Long> openIds = new HashSet<Long>();

    @Override
    public long getId(Connection connection) {
        long id = this.idCounter.incrementAndGet();
        synchronized (this) {
            this.openIds.add(id);
        }
        return id;
    }

    @Override
    public void addClosedId(long closedId) {
        synchronized (this) {
            this.openIds.remove(closedId);
        }
    }

    @Override
    public Set<Long> getOpenConnectionIds() {
        return new HashSet<Long>(this.openIds);
    }

}
