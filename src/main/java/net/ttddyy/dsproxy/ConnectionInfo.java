package net.ttddyy.dsproxy;

import java.sql.Connection;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.2
 */
public class ConnectionInfo {

    private String dataSourceName;
    private String connectionId;
    private int isolationLevel;
    private boolean isClosed;
    private int commitCount;
    private int rollbackCount;

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public int getIsolationLevel() {
        return isolationLevel;
    }

    public void setIsolationLevel(int isolationLevel) {
        this.isolationLevel = isolationLevel;
    }

    /**
     * Increment commit count.
     *
     * @since 1.4.5
     */
    public void incrementCommitCount() {
        this.commitCount++;
    }

    /**
     * Increment rollback count.
     *
     * @since 1.4.5
     */
    public void incrementRollbackCount() {
        this.rollbackCount++;
    }

    /**
     * Returns how many times {@link Connection#commit()} method is called.
     *
     * @return num of commit method being called
     * @since 1.4.5
     */
    public int getCommitCount() {
        return commitCount;
    }

    /**
     * @param commitCount num of commit method call
     * @since 1.4.5
     */
    public void setCommitCount(int commitCount) {
        this.commitCount = commitCount;
    }

    /**
     * Returns how many times {@link Connection#rollback()} method is called.
     *
     * @return num of rollback method being called
     * @since 1.4.5
     */
    public int getRollbackCount() {
        return rollbackCount;
    }

    /**
     * @param rollbackCount num of rollback method call
     * @since 1.4.5
     */
    public void setRollbackCount(int rollbackCount) {
        this.rollbackCount = rollbackCount;
    }

    /**
     * @since 1.4.5
     */
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * @since 1.4.5
     */
    public void setClosed(boolean closed) {
        isClosed = closed;
    }

}
