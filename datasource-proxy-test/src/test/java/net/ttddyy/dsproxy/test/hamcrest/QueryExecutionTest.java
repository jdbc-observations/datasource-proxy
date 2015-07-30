package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.QueryExecution;
import org.junit.Assert;
import org.junit.Test;

import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.fail;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.success;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueryExecutionTest {

    @Test
    public void testSuccess() {

        QueryExecution qe = new QueryExecution() {
            @Override
            public boolean isSuccess() {
                return true;
            }
        };

        Assert.assertThat(qe, success());
    }

    @Test
    public void testFail() {

        QueryExecution qe = new QueryExecution() {
            @Override
            public boolean isSuccess() {
                return false;
            }
        };

        Assert.assertThat(qe, fail());
    }
}
