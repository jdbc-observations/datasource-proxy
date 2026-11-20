package net.ttddyy.dsproxy.proxy;

import java.util.Arrays;
import java.util.Set;

import static net.ttddyy.dsproxy.proxy.ProxyLogicSupport.COMMON_METHOD_NAMES;

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

    static final String PARAMETER_METHOD_SET_NULL = "setNull";
    static final String PARAMETER_METHOD_REGISTER_OUT_PARAMETER = "registerOutParameter";
    static final String GET_GENERATED_KEYS_METHOD = "getGeneratedKeys";
    static final String GET_CONNECTION_METHOD = "getConnection";
    static final String GET_RESULTSET_METHOD = "getResultSet";

    static final Set<String> PARAMETER_METHODS = Set.of("setArray", "setAsciiStream", "setBigDecimal",
                    "setBinaryStream", "setBlob", "setBoolean", "setByte",
                    "setBytes", "setCharacterStream", "setClob", "setDate",
                    "setDouble", "setFloat", "setInt", "setLong",
                    PARAMETER_METHOD_SET_NULL,
                    "setObject", "setRef", "setShort",
                    "setString", "setTime", "setTimestamp", "setUnicodeStream", "setURL",
                    "setRowId", "setNString", "setNCharacterStream", "setNClob", "setSQLXML",
                    "clearParameters",
                    PARAMETER_METHOD_REGISTER_OUT_PARAMETER  // CallableStatement output parameter
            );

    static final Set<String> BATCH_PARAM_METHODS = Set.of("addBatch", "clearBatch");

    static final Set<String> BATCH_EXEC_METHODS = Set.of(
                    "executeBatch",
                    "executeLargeBatch"  // JDBC 4.2 (Java1.8)
            );
    static final Set<String> QUERY_EXEC_METHODS = Set.of(
                    "executeQuery", "executeUpdate", "execute",
                    "executeLargeUpdate"  // JDBC 4.2 (Java1.8)
            );
    static final Set<String> EXEC_METHODS = union(BATCH_EXEC_METHODS, QUERY_EXEC_METHODS);

    static final Set<String> JDBC4_METHODS = Set.of("unwrap", "isWrapperFor");

    static final Set<String> METHODS_TO_RETURN_RESULTSET = Set.of(
                    "executeQuery", GET_GENERATED_KEYS_METHOD, GET_RESULTSET_METHOD  // from Statement
            );

    static final Set<String> METHODS_TO_INTERCEPT = union(PARAMETER_METHODS, BATCH_PARAM_METHODS, EXEC_METHODS, JDBC4_METHODS, METHODS_TO_RETURN_RESULTSET, COMMON_METHOD_NAMES, Set.of(GET_CONNECTION_METHOD));

    static final Set<String> METHODS_TO_OPERATE_PARAMETER = union(PARAMETER_METHODS, BATCH_PARAM_METHODS);

    @SafeVarargs
    private static Set<String> union(Set<String>... sets) {
        return Set.copyOf(Arrays.stream(sets).flatMap(Set::stream).toList());
    }

}
