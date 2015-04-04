package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCount;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class DefaultQueryCountLogFormatterTest {

    @Test
    public void logFormat() {
        QueryCount queryCount = new QueryCount();
        queryCount.setTime(1);
        queryCount.setTotal(2);
        queryCount.setSuccess(3);
        queryCount.setFailure(4);
        queryCount.setSelect(5);
        queryCount.setInsert(6);
        queryCount.setUpdate(7);
        queryCount.setDelete(8);
        queryCount.setOther(9);
        String log = new DefaultQueryCountLogFormatter().getLogMessage("DS", queryCount);

        assertThat(log).isEqualTo("Name:\"DS\", Time:1, Total:2, Success:3, Failure:4, Select:5, Insert:6, Update:7, Delete:8, Other:9");
    }
}
