package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.QueryHolder;
import org.junit.Assert;
import org.junit.Test;

import static net.ttddyy.dsproxy.test.hamcrest.QueryHolderAssertions.query;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueryHolderTest {

    @Test
    public void testQuery() {
        QueryHolder queryHolder = mock(QueryHolder.class);
        given(queryHolder.getQuery()).willReturn("foo");

        Assert.assertThat(queryHolder, query(is("foo")));
        Assert.assertThat(queryHolder, query(startsWith("f")));
    }

    @Test
    public void testQueryMismatch() {
        QueryHolder queryHolder = mock(QueryHolder.class);
        given(queryHolder.getQuery()).willReturn("foo");

        try {
            Assert.assertThat(queryHolder, query(is("FOO")));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: query is \"FOO\"\n     but: query was \"foo\"");
        }
    }

}
