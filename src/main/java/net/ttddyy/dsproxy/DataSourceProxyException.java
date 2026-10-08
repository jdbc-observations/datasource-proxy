package net.ttddyy.dsproxy;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

/**
 * Exception thrown when datasource-proxy encounters an error.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class DataSourceProxyException extends RuntimeException {

    public DataSourceProxyException() {
    }

    public DataSourceProxyException(String message) {
        super(message);
    }

    public DataSourceProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataSourceProxyException(Throwable cause) {
        super(cause);
    }

    @IgnoreJRERequirement
    public DataSourceProxyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
