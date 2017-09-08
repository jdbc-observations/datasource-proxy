package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.DataSourceProxyException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tadaya Tsuyukubo
 * @author Liam Williams
 * @since 1.4.3
 */
public class ResultSetUtils {

    public static Map<String, Integer> columnNameToIndex(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            Map<String, Integer> columnNameToIndex = new HashMap<String, Integer>();
            for (int i = 1; i <= columnCount; i++) {
                columnNameToIndex.put(metaData.getColumnLabel(i), i);
            }
            return columnNameToIndex;
        } catch (SQLException e) {
            throw new DataSourceProxyException("Failed to obtain resultset metadata", e);
        }
    }

    public static int columnCount(ResultSet resultSet) {
        try {
            return resultSet.getMetaData().getColumnCount();
        } catch (SQLException e) {
            throw new DataSourceProxyException("Failed to obtain resultset metadata", e);
        }

    }

}
