package net.ttddyy.dsproxy;

import org.jspecify.annotations.Nullable;

import java.sql.Connection;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.2
 */
public class ConnectionInfo {

    private @Nullable String dataSourceName;
    private @Nullable String connectionId;
    private int isolationLevel;
    private boolean isClosed;
    private int commitCount;
    private int rollbackCount;

    public @Nullable String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(@Nullable String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public @Nullable String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(@Nullable String connectionId) {
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
     * Returns how many times {@link Connection#commit()} was called.
     *
     * @return number of commit calls
     * @since 1.4.5
     */
    public int getCommitCount() {
        return commitCount;
    }

    /**
     * @param commitCount number of commit calls
     * @since 1.4.5
     */
    public void setCommitCount(int commitCount) {
        this.commitCount = commitCount;
    }

    /**
     * Returns how many times {@link Connection#rollback()} was called.
     *
     * @return number of rollback calls
     * @since 1.4.5
     */
    public int getRollbackCount() {
        return rollbackCount;
    }

    /**
     * @param rollbackCount number of rollback calls
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
