package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.test.QueryHolder;
import org.junit.Assert;
import org.junit.Test;

import static net.ttddyy.dsproxy.test.hamcrest.QueryHolderAssertions.query;
import static net.ttddyy.dsproxy.test.hamcrest.QueryHolderAssertions.queryType;
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

    @Test
    public void testQueryType() {
        QueryHolder queryHolder = mock(QueryHolder.class);
        given(queryHolder.getQuery()).willReturn("SELECT");
        Assert.assertThat(queryHolder, queryType(QueryType.SELECT));

        queryHolder = mock(QueryHolder.class);
        given(queryHolder.getQuery()).willReturn("INSERT");
        Assert.assertThat(queryHolder, queryType(QueryType.INSERT));

        queryHolder = mock(QueryHolder.class);
        given(queryHolder.getQuery()).willReturn("UPDATE");
        Assert.assertThat(queryHolder, queryType(QueryType.UPDATE));
        queryHolder = mock(QueryHolder.class);
        given(queryHolder.getQuery()).willReturn("DELETE");
        Assert.assertThat(queryHolder, queryType(QueryType.DELETE));

        queryHolder = mock(QueryHolder.class);
        given(queryHolder.getQuery()).willReturn("OTHER");
        Assert.assertThat(queryHolder, queryType(QueryType.OTHER));
    }

    @Test
    public void testQueryTypeMismatch() {

        QueryHolder queryHolder = mock(QueryHolder.class);
        given(queryHolder.getQuery()).willReturn("insert into");

        try {
            Assert.assertThat(queryHolder, queryType(QueryType.SELECT));
            fail("assertion should fail");
        } catch (AssertionError e) {
            assertThat(e).hasMessage("\nExpected: query type is \"SELECT\"\n     but: query type was \"INSERT\" (insert into)");
        }

    }

}
