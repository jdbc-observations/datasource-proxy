package net.ttddyy.dsproxy.transform;

import java.sql.Statement;

/**
 * Holds context information for {@link ParameterTransformer#transformParameters(ParameterReplacer, TransformInfo)}.
 *
 * <ul>
 * <li>clazz: calling class, either {@link java.sql.PreparedStatement} or {@link java.sql.CallableStatement}
 * <li>dataSourceName: data source name
 * <li>query: query string
 * <li>isBatch: {@code true} when called as part of a batch
 * <li>count: current batch position, zero-based; {@code 0} if not batched
 * </ul>
 *
 * <p><b>Semantics of {@link #isBatch()}:</b>
 * <p>For {@link QueryTransformer}, {@link #isBatch()} is true only when {@link Statement#addBatch(String)} is called.
 * It is always false for {@link java.sql.PreparedStatement} and {@link java.sql.CallableStatement}.
 * For {@link ParameterTransformer}, it is true when {@link java.sql.PreparedStatement#addBatch()} or
 * {@link java.sql.CallableStatement#addBatch()} is called.
 *
 * @author Tadaya Tsuyukubo
 * @see net.ttddyy.dsproxy.transform.ParameterTransformer
 * @see net.ttddyy.dsproxy.transform.QueryTransformer
 * @since 1.2
 */
public class TransformInfo {

    private Class<? extends Statement> clazz;
    private String dataSourceName;
    private String query;
    private boolean isBatch;
    private int count;

    public TransformInfo() {
    }

    public TransformInfo(Class<? extends Statement> clazz, String dataSourceName, String query, boolean batch, int count) {
        this.clazz = clazz;
        this.dataSourceName = dataSourceName;
        this.query = query;
        isBatch = batch;
        this.count = count;
    }

    public Class<? extends Statement> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends Statement> clazz) {
        this.clazz = clazz;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isBatch() {
        return isBatch;
    }

    public void setBatch(boolean batch) {
        isBatch = batch;
    }

    /**
     * Returns the current order in the batch.
     * Zero-based; always {@code 0} when not called in batch mode.
     *
     * @return current order in the batch
     */
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
