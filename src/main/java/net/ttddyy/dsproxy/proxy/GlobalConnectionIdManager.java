package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionIdManager;

import java.sql.Connection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link ConnectionIdManager} implementation that emits connection IDs(sequential number) unique across all
 * datasources in JVM.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.6
 */
public class GlobalConnectionIdManager implements ConnectionIdManager {

    private static AtomicLong ID_COUNTER = new AtomicLong(0);

    private Set<String> openIds = Collections.synchronizedSet(new HashSet<String>());

    /**
     * Reset internal id counter.
     */
    public static void resetId() {
        ID_COUNTER.set(0);
    }

    @Override
    public String getId(Connection connection) {
        String id = String.valueOf(ID_COUNTER.incrementAndGet());
        this.openIds.add(id);
        return id;
    }

    @Override
    public void addClosedId(String closedId) {
        this.openIds.remove(closedId);
    }

    @Override
    public Set<String> getOpenConnectionIds() {
        return Collections.unmodifiableSet(this.openIds);
    }

}
