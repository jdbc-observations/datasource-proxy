package net.ttddyy.dsproxy.transform;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class NoOpParameterTransformer implements ParameterTransformer {

    public void transformParameters(ParameterReplacer replacer, TransformInfo transformInfo) {
        // do nothing
    }
}
