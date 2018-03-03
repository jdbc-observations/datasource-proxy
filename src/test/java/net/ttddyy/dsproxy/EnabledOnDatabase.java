package net.ttddyy.dsproxy;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable test when given database type is used.
 *
 * @author Tadaya Tsuyukubo
 * @see DbTestUtils
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith(EnabledOnDatabaseCondition.class)
public @interface EnabledOnDatabase {

    DatabaseType[] value();

}
