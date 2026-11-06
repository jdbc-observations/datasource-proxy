package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.QueryType;

/**
 * Uses the existing {@link QueryUtils#getQueryType(String)} classification.
 *
 * <p>This detector uses a leading-character heuristic, not a full SQL parser.
 */
public class DefaultQueryTypeDetector implements QueryTypeDetector {

    @Override
    public QueryType getQueryType(String query) {
        return QueryUtils.getQueryType(query);
    }

}
