package net.ttddyy.dsproxy.transform;

/**
 * No operation implementation of {@link QueryTransformer}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class NoOpQueryTransformer implements QueryTransformer {
    public String transformQuery(TransformInfo transformInfo) {
        return transformInfo.getQuery();
    }
}
