package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ConnectionInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChainConnectionAcquiringListener implements ConnectionAcquiringListener {
    private List<ConnectionAcquiringListener> listeners = new ArrayList<ConnectionAcquiringListener>();

    @Override
    public void beforeAcquireConnection(ConnectionInfo connectionInfo) {
        for (ConnectionAcquiringListener listener : listeners) {
            listener.beforeAcquireConnection(connectionInfo);
        }
    }

    @Override
    public void afterAcquireConnection(ConnectionInfo connectionInfo, long elapsedTime, Throwable throwable) {
        for (ConnectionAcquiringListener listener : listeners) {
            listener.afterAcquireConnection(connectionInfo, elapsedTime, throwable);
        }
    }

    @Override
    public void afterCommitConnection(ConnectionInfo connectionInfo, long elapsedTime, Throwable throwable) {
        for (ConnectionAcquiringListener listener : listeners) {
            listener.afterCommitConnection(connectionInfo, elapsedTime, throwable);
        }
    }

    @Override
    public void afterRollbackConnection(ConnectionInfo connectionInfo, long elapsedTime, Throwable throwable) {
        for (ConnectionAcquiringListener listener : listeners) {
            listener.afterRollbackConnection(connectionInfo, elapsedTime, throwable);
        }
    }

    @Override
    public void afterCloseConnection(ConnectionInfo connectionInfo, long elapsedTime, Throwable throwable) {
        for (ConnectionAcquiringListener listener : listeners) {
            listener.afterCloseConnection(connectionInfo, elapsedTime, throwable);
        }
    }

    public void addListener(ConnectionAcquiringListener listener) {
        this.listeners.add(listener);
    }

    public List<ConnectionAcquiringListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    public void setListeners(List<ConnectionAcquiringListener> listeners) {
        this.listeners = listeners;
    }
}
