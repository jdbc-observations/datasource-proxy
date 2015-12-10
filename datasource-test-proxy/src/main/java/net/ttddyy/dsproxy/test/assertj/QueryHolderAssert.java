package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.QueryHolder;
import org.assertj.core.api.AbstractCharSequenceAssert;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
class QueryHolderAssert extends AbstractCharSequenceAssert<QueryHolderAssert, String> {

    public QueryHolderAssert(QueryHolder actual) {
        super(actual.getQuery(), QueryHolderAssert.class);
    }

}
