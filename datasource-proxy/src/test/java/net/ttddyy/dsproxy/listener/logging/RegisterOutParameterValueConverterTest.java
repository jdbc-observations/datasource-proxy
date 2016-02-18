package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.listener.logging.RegisterOutParameterValueConverter;
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
                {Types.BIT, "OUTPUT(BIT[-7])"},
                {Types.TINYINT, "OUTPUT(TINYINT[-6])"},
                {Types.SMALLINT, "OUTPUT(SMALLINT[5])"},
                {Types.INTEGER, "OUTPUT(INTEGER[4])"},
                {Types.BIGINT, "OUTPUT(BIGINT[-5])"},
                {Types.FLOAT, "OUTPUT(FLOAT[6])"},
                {Types.REAL, "OUTPUT(REAL[7])"},
                {Types.DOUBLE, "OUTPUT(DOUBLE[8])"},
                {Types.NUMERIC, "OUTPUT(NUMERIC[2])"},
                {Types.DECIMAL, "OUTPUT(DECIMAL[3])"},
                {Types.CHAR, "OUTPUT(CHAR[1])"},
                {Types.VARCHAR, "OUTPUT(VARCHAR[12])"},
                {Types.LONGVARCHAR, "OUTPUT(LONGVARCHAR[-1])"},
                {Types.DATE, "OUTPUT(DATE[91])"},
                {Types.TIME, "OUTPUT(TIME[92])"},
                {Types.TIMESTAMP, "OUTPUT(TIMESTAMP[93])"},
                {Types.BINARY, "OUTPUT(BINARY[-2])"},
                {Types.VARBINARY, "OUTPUT(VARBINARY[-3])"},
                {Types.LONGVARBINARY, "OUTPUT(LONGVARBINARY[-4])"},
                {Types.NULL, "OUTPUT(NULL[0])"},
                {Types.OTHER, "OUTPUT(OTHER[1111])"},
                {Types.JAVA_OBJECT, "OUTPUT(JAVA_OBJECT[2000])"},
                {Types.DISTINCT, "OUTPUT(DISTINCT[2001])"},
                {Types.STRUCT, "OUTPUT(STRUCT[2002])"},
                {Types.ARRAY, "OUTPUT(ARRAY[2003])"},
                {Types.BLOB, "OUTPUT(BLOB[2004])"},
                {Types.CLOB, "OUTPUT(CLOB[2005])"},
                {Types.REF, "OUTPUT(REF[2006])"},
                {Types.DATALINK, "OUTPUT(DATALINK[70])"},
                {Types.BOOLEAN, "OUTPUT(BOOLEAN[16])"},
                {Types.ROWID, "OUTPUT(ROWID[-8])"},
                {Types.NCHAR, "OUTPUT(NCHAR[-15])"},
                {Types.NVARCHAR, "OUTPUT(NVARCHAR[-9])"},
                {Types.LONGNVARCHAR, "OUTPUT(LONGNVARCHAR[-16])"},
                {Types.NCLOB, "OUTPUT(NCLOB[2011])"},
                {Types.SQLXML, "OUTPUT(SQLXML[2009])"},
                {Types.REF_CURSOR, "OUTPUT(REF_CURSOR[2012])"},
                {Types.TIME_WITH_TIMEZONE, "OUTPUT(TIME_WITH_TIMEZONE[2013])"},
                {Types.TIMESTAMP_WITH_TIMEZONE, "OUTPUT(TIMESTAMP_WITH_TIMEZONE[2014])"},

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
