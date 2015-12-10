package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.CallableExecution;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.PreparedExecution;
import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.StatementExecution;

/**
 * Aggregated assertj assertions.
 *
 * @author Tadaya Tsuyukubo
 * @see ProxyTestDataSourceAssert
 * @see StatementExecutionAssert
 * @since 1.4
 */
public class DataSourceProxyAssertions {

    public static ProxyTestDataSourceAssert assertThat(ProxyTestDataSource actual) {
        return new ProxyTestDataSourceAssert(actual);
    }

    public static StatementExecutionAssert assertThat(StatementExecution actual) {
        return new StatementExecutionAssert(actual);
    }

    public static StatementBatchExecutionAssert assertThat(StatementBatchExecution actual) {
        return new StatementBatchExecutionAssert(actual);
    }

    public static PreparedExecutionAssert assertThat(PreparedExecution actual) {
        return new PreparedExecutionAssert(actual);
    }

    public static PreparedBatchExecutionAssert assertThat(PreparedBatchExecution actual) {
        return new PreparedBatchExecutionAssert(actual);
    }

    public static CallableExecutionAssert assertThat(CallableExecution actual) {
        return new CallableExecutionAssert(actual);
    }

    public static CallableBatchExecutionAssert assertThat(CallableBatchExecution actual) {
        return new CallableBatchExecutionAssert(actual);
    }

}
