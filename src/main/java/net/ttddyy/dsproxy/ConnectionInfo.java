package net.ttddyy.dsproxy;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.2
 */
public class ConnectionInfo {

    private String dataSourceName;
    private long connectionId;

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
}
