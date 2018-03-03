package net.ttddyy.dsproxy;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.Preconditions;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

/**
 * @author Tadaya Tsuyukubo
 * @see EnabledOnDatabase
 */
public class EnabledOnDatabaseCondition implements ExecutionCondition {

    private static final ConditionEvaluationResult ENABLED_BY_DEFAULT = enabled("@EnabledOnDatabase is not present");

    private static final ConditionEvaluationResult ENABLED = enabled("Enabled on database: " + TestUtils.dbType);

    private static final ConditionEvaluationResult DISABLED = disabled("Disabled on database: " + TestUtils.dbType);

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        Optional<AnnotatedElement> element = context.getElement();
        Optional<EnabledOnDatabase> annotation = findAnnotation(element, EnabledOnDatabase.class);
        if (annotation.isPresent()) {
            DatabaseType[] types = annotation.get().value();
            Preconditions.condition(types.length > 0, "value must be specified");
            return (Arrays.stream(types).anyMatch(TestUtils::isCurrentDbType)) ? ENABLED : DISABLED;
        }

        return ENABLED_BY_DEFAULT;
    }
}
