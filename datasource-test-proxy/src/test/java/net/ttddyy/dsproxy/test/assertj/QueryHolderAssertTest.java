package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.QueryHolder;
import org.junit.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueryHolderAssertTest {

    @Test
    public void query() {
        QueryHolder qh = mock(QueryHolder.class);
        given(qh.getQuery()).willReturn("select");

        new QueryHolderAssert(qh).isEqualTo("select");
        new QueryHolderAssert(qh).startsWith("s");
    }
}
