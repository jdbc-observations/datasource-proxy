package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.sql.JDBCType;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
@RunWith(Parameterized.class)
public class RegisterOutParameterValueConverterTest {

    private Object sqlType; // SQLType or int
    private String expected;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Types.BIT, "OUTPUT(BIT)"},
                {Types.TINYINT, "OUTPUT(TINYINT)"},
                {Types.SMALLINT, "OUTPUT(SMALLINT)"},
                {Types.INTEGER, "OUTPUT(INTEGER)"},
                {Types.BIGINT, "OUTPUT(BIGINT)"},
                {Types.FLOAT, "OUTPUT(FLOAT)"},
                {Types.REAL, "OUTPUT(REAL)"},
                {Types.DOUBLE, "OUTPUT(DOUBLE)"},
                {Types.NUMERIC, "OUTPUT(NUMERIC)"},
                {Types.DECIMAL, "OUTPUT(DECIMAL)"},
                {Types.CHAR, "OUTPUT(CHAR)"},
                {Types.VARCHAR, "OUTPUT(VARCHAR)"},
                {Types.LONGVARCHAR, "OUTPUT(LONGVARCHAR)"},
                {Types.DATE, "OUTPUT(DATE)"},
                {Types.TIME, "OUTPUT(TIME)"},
                {Types.TIMESTAMP, "OUTPUT(TIMESTAMP)"},
                {Types.BINARY, "OUTPUT(BINARY)"},
                {Types.VARBINARY, "OUTPUT(VARBINARY)"},
                {Types.LONGVARBINARY, "OUTPUT(LONGVARBINARY)"},
                {Types.NULL, "OUTPUT(NULL)"},
                {Types.OTHER, "OUTPUT(OTHER)"},
                {Types.JAVA_OBJECT, "OUTPUT(JAVA_OBJECT)"},
                {Types.DISTINCT, "OUTPUT(DISTINCT)"},
                {Types.STRUCT, "OUTPUT(STRUCT)"},
                {Types.ARRAY, "OUTPUT(ARRAY)"},
                {Types.BLOB, "OUTPUT(BLOB)"},
                {Types.CLOB, "OUTPUT(CLOB)"},
                {Types.REF, "OUTPUT(REF)"},
                {Types.DATALINK, "OUTPUT(DATALINK)"},
                {Types.BOOLEAN, "OUTPUT(BOOLEAN)"},
                {Types.ROWID, "OUTPUT(ROWID)"},
                {Types.NCHAR, "OUTPUT(NCHAR)"},
                {Types.NVARCHAR, "OUTPUT(NVARCHAR)"},
                {Types.LONGNVARCHAR, "OUTPUT(LONGNVARCHAR)"},
                {Types.NCLOB, "OUTPUT(NCLOB)"},
                {Types.SQLXML, "OUTPUT(SQLXML)"},
                {Types.REF_CURSOR, "OUTPUT(REF_CURSOR)"},
                {Types.TIME_WITH_TIMEZONE, "OUTPUT(TIME_WITH_TIMEZONE)"},
                {Types.TIMESTAMP_WITH_TIMEZONE, "OUTPUT(TIMESTAMP_WITH_TIMEZONE)"},

                {JDBCType.BIT, "OUTPUT(BIT)"},
                {JDBCType.TINYINT, "OUTPUT(TINYINT)"},
                {JDBCType.SMALLINT, "OUTPUT(SMALLINT)"},
                {JDBCType.INTEGER, "OUTPUT(INTEGER)"},
                {JDBCType.BIGINT, "OUTPUT(BIGINT)"},
                {JDBCType.FLOAT, "OUTPUT(FLOAT)"},
                {JDBCType.REAL, "OUTPUT(REAL)"},
                {JDBCType.DOUBLE, "OUTPUT(DOUBLE)"},
                {JDBCType.NUMERIC, "OUTPUT(NUMERIC)"},
                {JDBCType.DECIMAL, "OUTPUT(DECIMAL)"},
                {JDBCType.CHAR, "OUTPUT(CHAR)"},
                {JDBCType.VARCHAR, "OUTPUT(VARCHAR)"},
                {JDBCType.LONGVARCHAR, "OUTPUT(LONGVARCHAR)"},
                {JDBCType.DATE, "OUTPUT(DATE)"},
                {JDBCType.TIME, "OUTPUT(TIME)"},
                {JDBCType.TIMESTAMP, "OUTPUT(TIMESTAMP)"},
                {JDBCType.BINARY, "OUTPUT(BINARY)"},
                {JDBCType.VARBINARY, "OUTPUT(VARBINARY)"},
                {JDBCType.LONGVARBINARY, "OUTPUT(LONGVARBINARY)"},
                {JDBCType.NULL, "OUTPUT(NULL)"},
                {JDBCType.OTHER, "OUTPUT(OTHER)"},
                {JDBCType.JAVA_OBJECT, "OUTPUT(JAVA_OBJECT)"},
                {JDBCType.DISTINCT, "OUTPUT(DISTINCT)"},
                {JDBCType.STRUCT, "OUTPUT(STRUCT)"},
                {JDBCType.ARRAY, "OUTPUT(ARRAY)"},
                {JDBCType.BLOB, "OUTPUT(BLOB)"},
                {JDBCType.CLOB, "OUTPUT(CLOB)"},
                {JDBCType.REF, "OUTPUT(REF)"},
                {JDBCType.DATALINK, "OUTPUT(DATALINK)"},
                {JDBCType.BOOLEAN, "OUTPUT(BOOLEAN)"},
                {JDBCType.ROWID, "OUTPUT(ROWID)"},
                {JDBCType.NCHAR, "OUTPUT(NCHAR)"},
                {JDBCType.NVARCHAR, "OUTPUT(NVARCHAR)"},
                {JDBCType.LONGNVARCHAR, "OUTPUT(LONGNVARCHAR)"},
                {JDBCType.NCLOB, "OUTPUT(NCLOB)"},
                {JDBCType.SQLXML, "OUTPUT(SQLXML)"},
                {JDBCType.REF_CURSOR, "OUTPUT(REF_CURSOR)"},
                {JDBCType.TIME_WITH_TIMEZONE, "OUTPUT(TIME_WITH_TIMEZONE)"},
                {JDBCType.TIMESTAMP_WITH_TIMEZONE, "OUTPUT(TIMESTAMP_WITH_TIMEZONE)"},
        });
    }

    public RegisterOutParameterValueConverterTest(Object sqlType, String expected) {
        this.sqlType = sqlType;
        this.expected = expected;
    }

    @Test
    public void displayValue() {
        ParameterSetOperation param = new ParameterSetOperation();
        param.setArgs(new Object[]{null, this.sqlType});

        RegisterOutParameterValueConverter converter = new RegisterOutParameterValueConverter();
        String value = converter.getValue(param);

        assertThat(value).isEqualTo(this.expected);

    }
}
