package net.ttddyy.dsproxy.proxy;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public interface StatementMethodNames {

    static final Set<String> PARAMETER_METHODS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("setArray", "setAsciiStream", "setBigDecimal",
                    "setBinaryStream", "setBlob", "setBoolean", "setByte",
                    "setBytes", "setCharacterStream", "setClob", "setDate",
                    "setDouble", "setFloat", "setInt", "setLong",
                    "setNull", "setObject", "setRef", "setShort",
                    "setString", "setTime", "setTimestamp", "setUnicodeStream", "setURL",
                    "setRowId", "setNString", "setNCharacterStream", "setNClob", "setSQLXML",
                    "clearParameters",
                    "registerOutParameter"  // CallableStatement output parameter
            ))
    );

    static final Set<String> BATCH_PARAM_METHODS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("addBatch", "clearBatch"))
    );

    static final Set<String> EXEC_METHODS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("executeBatch", "executeQuery", "executeUpdate", "execute"))
    );

    static final Set<String> JDBC4_METHODS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("unwrap", "isWrapperFor"))
    );

    static final Set<String> GET_CONNECTION_METHOD = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("getConnection"))
    );

    static final Set<String> METHODS_TO_INTERCEPT = Collections.unmodifiableSet(
            new HashSet<String>() {
                {
                    addAll(PARAMETER_METHODS);
                    addAll(BATCH_PARAM_METHODS);
                    addAll(EXEC_METHODS);
                    addAll(JDBC4_METHODS);
                    addAll(GET_CONNECTION_METHOD);
                    add("getDataSourceName");
                    add("toString");
                    add("getTarget"); // from ProxyJdbcObject
                }
            }
    );

    static final Set<String> METHODS_TO_OPERATE_PARAMETER = Collections.unmodifiableSet(
            new HashSet<String>() {
                {
                    addAll(PARAMETER_METHODS);
                    addAll(BATCH_PARAM_METHODS);
                }
            }
    );


}
