package net.ttddyy.dsproxy.transform;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class NoOpParameterTransformer implements ParameterTransformer {

    public void transformParameters(String dataSourceName, String query, ParameterReplacer replacer) {
        // do nothing
    }
}
