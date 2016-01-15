package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.QueryHolder;
import org.assertj.core.api.AbstractCharSequenceAssert;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
class QueryAssert extends AbstractCharSequenceAssert<QueryAssert, String> {

    public QueryAssert(QueryHolder actual) {
        super(actual.getQuery(), QueryAssert.class);
    }

}
