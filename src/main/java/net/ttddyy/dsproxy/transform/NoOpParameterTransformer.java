package net.ttddyy.dsproxy.transform;

/**
 * No operation implementation of {@link ParameterTransformer}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public class NoOpParameterTransformer implements ParameterTransformer {

    public void transformParameters(ParameterReplacer replacer, TransformInfo transformInfo) {
        // do nothing
    }
}
