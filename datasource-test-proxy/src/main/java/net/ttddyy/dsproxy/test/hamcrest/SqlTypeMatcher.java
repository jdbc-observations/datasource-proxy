package net.ttddyy.dsproxy.test.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Hamcrest matcher for {@link java.sql.Types}(int).
 *
 * @author Tadaya Tsuyukubo
 * @see java.sql.Types
 * @since 1.4
 */
public class SqlTypeMatcher extends TypeSafeMatcher<Integer> {

    public static final Map<Integer, String> typeNameByValue = new HashMap<Integer, String>();

    static {
        try {
            Field[] fields = Types.class.getDeclaredFields();
            for (Field field : fields) {
                typeNameByValue.put(field.getInt(null), field.getName());
            }
        } catch (Exception e) {
        }
    }

    protected int expectedSqlType;
    protected String messagePrefix = "";
    protected String messageSuffix = "";

    public SqlTypeMatcher(int expectedSqlType) {
        this.expectedSqlType = expectedSqlType;
    }

    public SqlTypeMatcher(int expectedSqlType, String messagePrefix, String messageSuffix) {
        this.expectedSqlType = expectedSqlType;
        this.messagePrefix = messagePrefix;
        this.messageSuffix = messageSuffix;
    }

    @Override
    protected boolean matchesSafely(Integer item) {
        if (item == null) {
            return false;
        }
        return this.expectedSqlType == item;
    }


    @Override
    public void describeTo(Description description) {
        // expected message
        description.appendText(getMessage(this.expectedSqlType));
    }

    @Override
    protected void describeMismatchSafely(Integer item, Description mismatchDescription) {
        // for actual(but was) message
        mismatchDescription.appendText(getMessage(item));
    }

    protected String getMessage(Integer sqlType) {
        String typeName = typeNameByValue.get(sqlType);
        if (typeName == null) {
            typeName = "UNKNOWN";
        }
        return this.messagePrefix + typeName + ":" + sqlType + this.messageSuffix;
    }

    public void setMessagePrefix(String messagePrefix) {
        this.messagePrefix = messagePrefix;
    }

    public void setMessageSuffix(String messageSuffix) {
        this.messageSuffix = messageSuffix;
    }
}
