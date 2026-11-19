package net.ttddyy.dsproxy.proxy;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
class MutableQueryExecutionContextTest {

    @Test
    void defaultsAreSafeBeforeExecutionCompletes() {
        // listeners read these before the query finishes (e.g. SlowQueryListener, logging in beforeQuery)
        MutableQueryExecutionContext context = new MutableQueryExecutionContext();
        assertThat(context.getElapsedTime()).isEqualTo(Duration.ZERO);
        assertThat(context.getQueries()).isEmpty();
        assertThat(context.getCustomValue("missing", String.class)).isNull();
    }

}
