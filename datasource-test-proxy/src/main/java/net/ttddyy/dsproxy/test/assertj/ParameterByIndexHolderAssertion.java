package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.ParameterByIndexHolder;
import org.assertj.core.api.AbstractAssert;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
class ParameterByIndexHolderAssertion extends AbstractAssert<ParameterByIndexHolderAssertion, ParameterByIndexHolder> {

    public ParameterByIndexHolderAssertion(ParameterByIndexHolder actual) {
        super(actual, ParameterByIndexHolderAssertion.class);
    }

    public static ParameterByIndexHolderAssertion assertThat(ParameterByIndexHolder actual) {
        return new ParameterByIndexHolderAssertion(actual);
    }

    public ParameterByIndexHolderAssertion foo(){
        return this;
    }

}
