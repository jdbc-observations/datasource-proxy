package net.ttddyy.dsproxy.transform;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.2
 */
public interface ParameterTransformer {

    static ParameterTransformer DEFAULT = new NoOpParameterTransformer();

    void transformParameters(ParameterReplacer replacer, TransformInfo transformInfo);
}
