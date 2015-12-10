package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.PreparedExecution;
import org.assertj.core.api.AbstractAssert;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class PreparedExecutionAssert extends AbstractAssert<PreparedExecutionAssert, PreparedExecution> {

    public PreparedExecutionAssert(PreparedExecution actual) {
        super(actual, PreparedExecutionAssert.class);
    }

    public PreparedExecutionAssert aa(){
        new ParameterByIndexHolderAssertion(this.actual).foo();
        return this;
    }
    public PreparedExecutionAssert bb(){
//        new QueryHolderAssert(this.actual).foo("abc");
        return this;
    }

}
