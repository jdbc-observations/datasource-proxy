package net.ttddyy.dsproxy.transform;

/**
 * Interceptor that can transform the query statement.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public interface QueryTransformer {

    static QueryTransformer DEFAULT = new NoOpQueryTransformer();

    String transformQuery(TransformInfo transformInfo);
}
