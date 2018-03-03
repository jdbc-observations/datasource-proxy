package net.ttddyy.dsproxy;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Tadaya Tsuyukubo
 */
public enum DatabaseType {
    HSQL, POSTGRES, MYSQL;

    static Optional<DatabaseType> valueOfIgnoreCase(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
