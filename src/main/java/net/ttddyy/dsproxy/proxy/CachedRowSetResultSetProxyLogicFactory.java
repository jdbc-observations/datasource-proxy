package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.DataSourceProxyException;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Factory for creating {@link CachedRowSetResultSetProxyLogic}.
 *
 * <p>Provides a {@link ResultSet} proxy backed by a {@link CachedRowSet}, which supports disconnected scrollability.
 *
 * <p>This class uses {@link RowSetFactory}, which requires JDK 1.7 or later, to create a {@link CachedRowSet}.
 * You can change the creation strategy by using a different {@link RowSetFactory}, extending this class, or
 * implementing another {@link ResultSetProxyLogicFactory}.
 *
 * The default {@link CachedRowSet} implementation is {@code com.sun.rowset.CachedRowSetImpl}.
 *
 * @author Tadaya Tsuyukubo
 * @see RowSetFactory
 * @see CachedRowSet
 * @see CachedRowSetResultSetProxyLogic
 * @since 1.4.7
 */
@IgnoreJRERequirement
public class CachedRowSetResultSetProxyLogicFactory implements ResultSetProxyLogicFactory {

    private final RowSetFactory rowSetFactory;

    public CachedRowSetResultSetProxyLogicFactory() {
        try {
            this.rowSetFactory = RowSetProvider.newFactory();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ResultSetProxyLogic create(ResultSet resultSet, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        ResultSet cachedRowSet = getCachedRowSet(resultSet);
        return new CachedRowSetResultSetProxyLogic(resultSet, cachedRowSet, connectionInfo, proxyConfig);
    }

    protected ResultSet getCachedRowSet(ResultSet resultSet) {
        try {
            // CachedRowSet only works with a non-null ResultSet.
            if (resultSet.getMetaData().getColumnCount() > 0) {
                CachedRowSet cachedRowSet = this.rowSetFactory.createCachedRowSet();
                cachedRowSet.populate(resultSet);
                return cachedRowSet;
            } else {
                // For an empty result set, return the original result set.
                return resultSet;
            }
        } catch (SQLException e) {
            throw new DataSourceProxyException("Failed to create CachedRowSet", e);
        }
    }

}
