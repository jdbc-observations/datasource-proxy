package net.ttddyy.dsproxy;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.2
 */
public class ConnectionInfo {

    private String dataSourceName;
    private long connectionId;
    private boolean isClosed;

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(long connectionId) {
        this.connectionId = connectionId;
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
