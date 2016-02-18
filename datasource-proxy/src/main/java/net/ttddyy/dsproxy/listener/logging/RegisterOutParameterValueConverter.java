package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

/**
 * Converter for registerOutParameter parameter operations in {@link java.sql.CallableStatement}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class RegisterOutParameterValueConverter implements ParameterValueConverter {

    @Override
    public String getValue(ParameterSetOperation param) {
        Object sqlType = param.getArgs()[1];  // 2nd arg is sqlType
        return this.getDisplayValue(sqlType);
    }

    public String getDisplayValue(Object sqlType) {
        StringBuilder sb = new StringBuilder();
        sb.append("OUTPUT(");
        // for the second argument, it is either int or SQLType(in JDBC 4.2)
        if (sqlType instanceof Integer) {
            String sqlTypeName = ParameterValueConverter.SQL_TYPENAME_BY_CODE.get((Integer) sqlType);
            if (sqlTypeName != null) {
                sb.append(sqlTypeName);
                sb.append("[");
                sb.append(sqlType);
                sb.append("]");
            } else {
                sb.append(sqlType);
            }
        } else {
            sb.append(sqlType);
        }

        sb.append(")");
        return sb.toString();
    }

}
