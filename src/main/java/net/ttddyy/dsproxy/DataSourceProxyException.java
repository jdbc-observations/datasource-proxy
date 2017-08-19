package net.ttddyy.dsproxy;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

/**
 * Framework exception that datasource-proxy encountered.
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
