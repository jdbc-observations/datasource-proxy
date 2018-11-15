package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

/**
 * Convert {@link ParameterSetOperation} to String.
 *
 * This is used for normal parameter set operations, not for setNull or registerOutParameter operations.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public class SimpleParameterValueConverter implements ParameterValueConverter {

    @Override
    public String getValue(ParameterSetOperation param) {
        Object value = param.getArgs()[1];  // second argument is the value to display
        return value == null ? null : value.toString();
    }

}
