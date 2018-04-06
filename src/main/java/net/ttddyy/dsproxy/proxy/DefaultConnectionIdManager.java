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

    private Set<String> openIds = new HashSet<String>();

    @Override
    public String getId(Connection connection) {
        String id = String.valueOf(this.idCounter.incrementAndGet());
        synchronized (this) {
            this.openIds.add(id);
        }
        return id;
    }

    @Override
    public void addClosedId(String closedId) {
        synchronized (this) {
            this.openIds.remove(closedId);
        }
    }

    @Override
    public Set<String> getOpenConnectionIds() {
        return new HashSet<String>(this.openIds);
    }

}
