package net.ttddyy.dsproxy.transform;

/**
 * No operation implementation of {@code QueryTransformer}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class NoOpQueryTransformer implements QueryTransformer {
    public String transformQuery(final String dataSourceName, final String query) {
        return query;
    }
}
