package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.QueryHolder;
import org.junit.Assert;
import org.junit.Test;

import static net.ttddyy.dsproxy.test.hamcrest.QueryHolderAssertions.query;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueryHolderTest {

    @Test
    public void testQuery() {
        QueryHolder queryHolder = new QueryHolder() {
            @Override
            public String getQuery() {
                return "foo";
            }
        };

        Assert.assertThat(queryHolder, query(is("foo")));
        Assert.assertThat(queryHolder, query(startsWith("f")));
    }

}
