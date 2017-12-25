package net.ttddyy.dsproxy.proxy.delegate;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class DelegatingProxyTest {

    @Test
    public void methodFields() {
        // check all method fields defined in delegating classes are correct.

        // connection
        List<Field> connectionFields = getDeclaredMethodFields(DelegatingConnection.class);
        assertThat(connectionFields).doesNotContainNull().doesNotHaveDuplicates();

        // datasource
        List<Field> datasourceFields = getDeclaredMethodFields(DelegatingDataSource.class);
        assertThat(datasourceFields).doesNotContainNull().doesNotHaveDuplicates();

        // statement
        List<Field> statementFields = getDeclaredMethodFields(DelegatingStatement.class);
        assertThat(statementFields).doesNotContainNull().doesNotHaveDuplicates();

        // prepared-statement
        List<Field> preparedFields = getDeclaredMethodFields(DelegatingPreparedStatement.class);
        assertThat(preparedFields).doesNotContainNull().doesNotHaveDuplicates();

        // callbale-statement
        List<Field> callableFields = getDeclaredMethodFields(DelegatingCallableStatement.class);
        assertThat(callableFields).doesNotContainNull().doesNotHaveDuplicates();

        // resultset
        List<Field> resultSetFields = getDeclaredMethodFields(DelegatingResultSet.class);
        assertThat(resultSetFields).doesNotContainNull().doesNotHaveDuplicates();

        // check uniqueness
        List<Field> allFields = new ArrayList<Field>();
        allFields.addAll(connectionFields);
        allFields.addAll(datasourceFields);
        allFields.addAll(statementFields);
        allFields.addAll(preparedFields);
        allFields.addAll(callableFields);
        allFields.addAll(resultSetFields);

        assertThat(allFields).doesNotHaveDuplicates();
    }

    private List<Field> getDeclaredMethodFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();
        for (Field field : clazz.getDeclaredFields()) {
            // only add static field that type is Method
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == Method.class) {
                continue;
            }
            fields.add(field);
        }

        return fields;
    }
}
