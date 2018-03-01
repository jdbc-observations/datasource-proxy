package net.ttddyy.dsproxy.proxy;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Method names that proxy logic classes have interested in.
 *
 * @author Tadaya Tsuyukubo
 * @see java.sql.Statement
 * @see java.sql.PreparedStatement
 * @see java.sql.CallableStatement
 * @since 1.2
 */
public interface StatementMethodNames {

    String PARAMETER_METHOD_SET_NULL = "setNull";
    String PARAMETER_METHOD_REGISTER_OUT_PARAMETER = "registerOutParameter";
    String GET_GENERATED_KEYS_METHOD = "getGeneratedKeys";
    String GET_CONNECTION_METHOD = "getConnection";

    Set<String> PARAMETER_METHODS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("setArray", "setAsciiStream", "setBigDecimal",
                    "setBinaryStream", "setBlob", "setBoolean", "setByte",
                    "setBytes", "setCharacterStream", "setClob", "setDate",
                    "setDouble", "setFloat", "setInt", "setLong",
                    PARAMETER_METHOD_SET_NULL,
                    "setObject", "setRef", "setShort",
                    "setString", "setTime", "setTimestamp", "setUnicodeStream", "setURL",
                    "setRowId", "setNString", "setNCharacterStream", "setNClob", "setSQLXML",
                    "clearParameters",
                    PARAMETER_METHOD_REGISTER_OUT_PARAMETER  // CallableStatement output parameter
            ))
    );

    Set<String> BATCH_PARAM_METHODS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("addBatch", "clearBatch"))
    );

    Set<String> BATCH_EXEC_METHODS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    "executeBatch",
                    "executeLargeBatch"  // JDBC 4.2 (Java1.8)
            ))
    );
    Set<String> QUERY_EXEC_METHODS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    "executeQuery", "executeUpdate", "execute",
                    "executeLargeUpdate"  // JDBC 4.2 (Java1.8)
            ))
    );
    Set<String> EXEC_METHODS = Collections.unmodifiableSet(
            new HashSet<String>() {
                {
                    addAll(BATCH_EXEC_METHODS);
                    addAll(QUERY_EXEC_METHODS);
                }
            }
    );

    Set<String> JDBC4_METHODS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("unwrap", "isWrapperFor"))
    );

    Set<String> METHODS_TO_RETURN_RESULTSET = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    "executeQuery", GET_GENERATED_KEYS_METHOD, "getResultSet",  // from Statement
                    "executeQuery"  // from PreparedStatement
            ))
    );

    Set<String> METHODS_TO_INTERCEPT = Collections.unmodifiableSet(
            new HashSet<String>() {
                {
                    addAll(PARAMETER_METHODS);
                    addAll(BATCH_PARAM_METHODS);
                    addAll(EXEC_METHODS);
                    addAll(JDBC4_METHODS);
                    addAll(METHODS_TO_RETURN_RESULTSET);
                    add(GET_CONNECTION_METHOD);
                    add("getDataSourceName");
                    add("toString");
                    add("getTarget"); // from ProxyJdbcObject
                }
            }
    );

    Set<String> METHODS_TO_OPERATE_PARAMETER = Collections.unmodifiableSet(
            new HashSet<String>() {
                {
                    addAll(PARAMETER_METHODS);
                    addAll(BATCH_PARAM_METHODS);
                }
            }
    );


}
