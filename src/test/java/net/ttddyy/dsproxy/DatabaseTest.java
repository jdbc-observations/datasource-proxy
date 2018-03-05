package net.ttddyy.dsproxy;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Inject {@link DbResourceCleaner}.
 * close all resources registered on {@link DbResourceCleaner} after each test method.
 *
 * @author Tadaya Tsuyukubo
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith(DatabaseTest.DatabaseTestExtension.class)
public @interface DatabaseTest {

    class DatabaseTestExtension implements ParameterResolver, AfterEachCallback {

        private DbResourceCleaner cleaner = new DbResourceCleaner();

        @Override
        public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
            return parameterContext.getParameter().getType() == DbResourceCleaner.class;
        }

        @Override
        public DbResourceCleaner resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
            return this.cleaner;
        }

        @Override
        public void afterEach(ExtensionContext context) throws Exception {
            this.cleaner.closeAll();
        }

    }

}
