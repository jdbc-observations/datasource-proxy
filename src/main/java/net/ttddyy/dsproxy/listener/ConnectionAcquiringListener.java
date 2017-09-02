package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ConnectionInfo;

public interface ConnectionAcquiringListener {

    void beforeAcquireConnection(ConnectionInfo connectionInfo);

    void afterAcquireConnection(ConnectionInfo connectionInfo, long elapsedTime, Throwable throwable);

    void afterCommitConnection(ConnectionInfo connectionInfo, long elapsedTime, Throwable throwable);

    void afterRollbackConnection(ConnectionInfo connectionInfo, long elapsedTime, Throwable throwable);

    void afterCloseConnection(ConnectionInfo connectionInfo, long elapsedTime, Throwable throwable);
}
