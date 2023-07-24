package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.DataSourceProxyException;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Factory to create {@link SpringSqlRowSetProxyLogic}.
 *
 * @author a-simeshin
 * @since 1.10
 */
public class SpringSqlRowSetProxyLogicFactory implements ResultSetProxyLogicFactory {

    @Override
    public ResultSetProxyLogic create(ResultSet resultSet, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        final ResultSetWrappingSqlRowSet cachedRowSet = getCachedRowSet(resultSet);
        return new SpringSqlRowSetProxyLogic(resultSet, cachedRowSet, connectionInfo, proxyConfig);
    }

    /**
     * Retrieves a cached row set from the given ResultSet. If the ResultSet contains no columns,
     * a ResultSetWrappingSqlRowSet is created directly from the ResultSet. Otherwise, a CachedRowSet
     * is created using RowSetProvider and populated with the data from the ResultSet, and then wrapped
     * in a ResultSetWrappingSqlRowSet.
     *
     * @param resultSet The ResultSet from which to create the cached row set.
     * @return The cached row set as a ResultSetWrappingSqlRowSet.
     * @throws DataSourceProxyException If an error occurs while creating the CachedRowSet.
     */
    protected ResultSetWrappingSqlRowSet getCachedRowSet(ResultSet resultSet) {
        try {
            if (resultSet.getMetaData().getColumnCount() == 0) {
                return new ResultSetWrappingSqlRowSet(resultSet);
            }
            final RowSetFactory rowSetFactory = RowSetProvider.newFactory();
            final CachedRowSet cachedRowSet = rowSetFactory.createCachedRowSet();
            cachedRowSet.populate(resultSet);
            return new ResultSetWrappingSqlRowSet(cachedRowSet);
        } catch (SQLException e) {
            throw new DataSourceProxyException("Failed to create ResultSetWrappingSqlRowSet", e);
        }
    }

}
