package net.ttddyy.dsproxy.test.assertj.helper;

import org.assertj.core.api.WritableAssertionInfo;
import org.assertj.core.error.MessageFormatter;
import org.assertj.core.internal.Failures;

/**
 * @author Tadaya Tsuyukubo
 */
// TODO: better name
public abstract class AbstractHelperAsserts {
    protected WritableAssertionInfo info;

    public AbstractHelperAsserts(WritableAssertionInfo info) {
        this.info = info;
    }

    // TODO: it's copy from assertj, should find better way.
    protected void failWithMessage(String errorMessage, Object... arguments) {
        AssertionError failureWithOverriddenErrorMessage = Failures.instance().failureIfErrorMessageIsOverridden(info);
        if (failureWithOverriddenErrorMessage != null) throw failureWithOverriddenErrorMessage;
        String description = MessageFormatter.instance().format(info.description(), info.representation(), "");
        throw new AssertionError(description + String.format(errorMessage, arguments));
    }

}
