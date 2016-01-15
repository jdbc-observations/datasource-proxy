package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.QueryHolder;
import org.junit.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueryAssertTest {

    @Test
    public void query() {
        QueryHolder qh = mock(QueryHolder.class);
        given(qh.getQuery()).willReturn("select");

        new QueryAssert(qh).isEqualTo("select");
        new QueryAssert(qh).startsWith("s");
    }
}
