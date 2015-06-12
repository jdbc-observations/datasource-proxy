package net.ttddyy.dsproxy.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Keeps a method and its arguments when parameter-set-method is called.
 *
 * @author Tadaya Tsuyukubo
 * @see net.ttddyy.dsproxy.proxy.jdk.PreparedStatementInvocationHandler
 * @since 1.2
 */
public class ParameterSetOperation {

    // key: int(code) specified in java.sql.Types, value: corresponding fieldname
    private static final Map<Integer, String> SQL_TYPE_NAME_BY_CODE;

    static {
        SQL_TYPE_NAME_BY_CODE = new HashMap<Integer, String>();
        try {
            Class<?> clazz = java.sql.Types.class;
            for (Field field : clazz.getFields()) {
                String name = field.getName();
                int code = field.getInt(clazz);
                SQL_TYPE_NAME_BY_CODE.put(code, name.toUpperCase());
            }
        } catch (Exception e) {
        }
    }

    private Method method;
    private Object[] args;

    public ParameterSetOperation() {
    }

    public ParameterSetOperation(Method method, Object[] args) {
        this.method = method;
        this.args = args;
    }

    /**
     * ParameterIndex from PreparedStatement or ParameterName from CallableStatement (first argument).
     * <p> <em>NOTE:</em> This method is a quick fix for logging "setNull" value.
     * This method will be modified or removed soon.
     *
     * @return parameterIndex or parameterName as String
     * @since 1.3.1
     */
    public String getParameterNameOrIndexAsString() {
        Object key = this.args[0];  // either int(parameterIndex) or string(parameterName)
        return key instanceof String ? (String) key : key.toString();
    }

    /**
     * Value to display in logging from parameter-set-method.
     * <p> There is special handling for "setNull" method now.
     * <p> <em>NOTE:</em> This method is a quick fix for logging "setNull" value.
     * This method will be modified or removed soon.
     *
     * @return parameterIndex or parameterName as String
     * @since 1.3.1
     */
    public Object getParameterValue() {
        Object value = this.args[1];

        String methodName = method.getName();
        // quick hack for "setNull" and "registerOutParameter" operation
        if (StatementMethodNames.PARAMETER_METHOD_SET_NULL.equals(methodName)) {
            // second arg for setNull is always int
            Integer sqlType = (Integer) value;
            String sqlTypeName = SQL_TYPE_NAME_BY_CODE.get(sqlType);

            if (sqlTypeName != null) {
                value = "NULL(" + sqlTypeName + ")";
            } else {
                value = "NULL";  // for unrecognized code
            }
        }
        return value;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
