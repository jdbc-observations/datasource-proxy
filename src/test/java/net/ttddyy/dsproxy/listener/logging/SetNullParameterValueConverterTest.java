package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class SetNullParameterValueConverterTest {

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Types.BIT, "NULL(BIT)"},
                {Types.TINYINT, "NULL(TINYINT)"},
                {Types.SMALLINT, "NULL(SMALLINT)"},
                {Types.INTEGER, "NULL(INTEGER)"},
                {Types.BIGINT, "NULL(BIGINT)"},
                {Types.FLOAT, "NULL(FLOAT)"},
                {Types.REAL, "NULL(REAL)"},
                {Types.DOUBLE, "NULL(DOUBLE)"},
                {Types.NUMERIC, "NULL(NUMERIC)"},
                {Types.DECIMAL, "NULL(DECIMAL)"},
                {Types.CHAR, "NULL(CHAR)"},
                {Types.VARCHAR, "NULL(VARCHAR)"},
                {Types.LONGVARCHAR, "NULL(LONGVARCHAR)"},
                {Types.DATE, "NULL(DATE)"},
                {Types.TIME, "NULL(TIME)"},
                {Types.TIMESTAMP, "NULL(TIMESTAMP)"},
                {Types.BINARY, "NULL(BINARY)"},
                {Types.VARBINARY, "NULL(VARBINARY)"},
                {Types.LONGVARBINARY, "NULL(LONGVARBINARY)"},
                {Types.NULL, "NULL(NULL)"},
                {Types.OTHER, "NULL(OTHER)"},
                {Types.JAVA_OBJECT, "NULL(JAVA_OBJECT)"},
                {Types.DISTINCT, "NULL(DISTINCT)"},
                {Types.STRUCT, "NULL(STRUCT)"},
                {Types.ARRAY, "NULL(ARRAY)"},
                {Types.BLOB, "NULL(BLOB)"},
                {Types.CLOB, "NULL(CLOB)"},
                {Types.REF, "NULL(REF)"},
                {Types.DATALINK, "NULL(DATALINK)"},
                {Types.BOOLEAN, "NULL(BOOLEAN)"},
                {Types.ROWID, "NULL(ROWID)"},
                {Types.NCHAR, "NULL(NCHAR)"},
                {Types.NVARCHAR, "NULL(NVARCHAR)"},
                {Types.LONGNVARCHAR, "NULL(LONGNVARCHAR)"},
                {Types.NCLOB, "NULL(NCLOB)"},
                {Types.SQLXML, "NULL(SQLXML)"},
                {Types.REF_CURSOR, "NULL(REF_CURSOR)"},
                {Types.TIME_WITH_TIMEZONE, "NULL(TIME_WITH_TIMEZONE)"},
                {Types.TIMESTAMP_WITH_TIMEZONE, "NULL(TIMESTAMP_WITH_TIMEZONE)"},
        });
    }

    @ParameterizedTest
    @MethodSource("data")
    public void displayValue(Object sqlType, String expected) {
        ParameterSetOperation param = new ParameterSetOperation();
        param.setArgs(new Object[]{null, sqlType});

        SetNullParameterValueConverter converter = new SetNullParameterValueConverter();
        String value = converter.getValue(param);

        assertThat(value).isEqualTo(expected);
    }

}
