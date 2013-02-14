package net.ttddyy.dsproxy.transform;

import java.sql.Statement;

/**
 * Hold context information for {@link ParameterTransformer#transformParameters(ParameterReplacer, TransformInfo)}.
 * <p/>
 * <ul>
 * <li>clazz: calling class. {@link java.sql.PreparedStatement} or {@link java.sql.CallableStatement}</li>
 * <li>dataSourceName: datasource name</li>
 * <li>query: query string</li>
 * <li>isBatch: true when called in batch</li>
 * <li>count: current number of call in batch. 0 origin. 0 if call is not batched </li>
 * </ul>
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
     * Current order in batch.
     * 0 origin. always 0 if called in non-batch.
     *
     * @return current order in batch
     */
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
