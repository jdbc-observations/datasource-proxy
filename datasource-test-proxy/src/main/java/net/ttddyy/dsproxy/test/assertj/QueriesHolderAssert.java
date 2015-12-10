package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.QueriesHolder;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ListAssert;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class QueriesHolderAssert extends AbstractListAssert<ListAssert<String>, List<? extends String>, String> {

    public QueriesHolderAssert(QueriesHolder actual) {
        super(actual.getQueries(), QueriesHolderAssert.class);
    }
}
