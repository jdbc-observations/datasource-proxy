package net.ttddyy.dsproxy.listener;

import org.jspecify.annotations.Nullable;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.ProxyJdbcObject;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * POJO to hold values for JDBC API invocations.
 *
 * @author Tadaya Tsuyukubo
 * @see ProxyDataSourceListener
 * @since 1.4.3
 */
public class MethodExecutionContext {

    public static class Builder {
        private @Nullable Object target;
        private @Nullable Method method;
        private Object @Nullable [] methodArgs;
        private @Nullable Object result;
        private @Nullable Throwable thrown;
        private Duration elapsedTime = Duration.ZERO;
        private @Nullable ConnectionInfo connectionInfo;
        private @Nullable ProxyConfig proxyConfig;
        private @Nullable Object proxy;

        public static Builder create() {
            return new Builder();
        }

        public MethodExecutionContext build() {
            MethodExecutionContext context = new MethodExecutionContext();
            context.target = this.target;
            context.method = this.method;
            context.methodArgs = this.methodArgs;
            context.result = this.result;
            context.thrown = this.thrown;
            context.elapsedTime = this.elapsedTime;
            context.connectionInfo = this.connectionInfo;
            context.proxyConfig = this.proxyConfig;
            context.proxy = this.proxy;
            return context;
        }

        public Builder target(Object target) {
            this.target = target;
            return this;
        }

        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        public Builder methodArgs(Object[] methodArgs) {
            this.methodArgs = methodArgs;
            return this;
        }

        public Builder result(Object result) {
            this.result = result;
            return this;
        }

        public Builder thrown(Throwable thrown) {
            this.thrown = thrown;
            return this;
        }

        public Builder elapsedTime(Duration elapsedTime) {
            this.elapsedTime = elapsedTime;
            return this;
        }

        public Builder connectionInfo(ConnectionInfo connectionInfo) {
            this.connectionInfo = connectionInfo;
            return this;
        }

        public Builder proxyConfig(ProxyConfig proxyConfig) {
            this.proxyConfig = proxyConfig;
            return this;
        }

        public Builder proxy(Object proxy) {
            this.proxy = proxy;
            return this;
        }
    }


    private @Nullable Object target;
    private @Nullable Method method;
    private Object @Nullable [] methodArgs;
    private @Nullable Object result;
    private @Nullable Throwable thrown;
    private Duration elapsedTime = Duration.ZERO;
    private @Nullable ConnectionInfo connectionInfo;
    private @Nullable ProxyConfig proxyConfig;
    private @Nullable Object proxy;
    private Map<String, Object> customValues = new HashMap<String, Object>();

    public @Nullable Object getTarget() {
        return target;
    }

    public void setTarget(@Nullable Object target) {
        this.target = target;
    }

    public @Nullable Method getMethod() {
        return method;
    }

    /**
     * Set a method to invoke.
     *
     * If new method is set on {@link ProxyDataSourceListener#beforeMethod(MethodExecutionContext)}, the newly set
     * method will be invoked.
     *
     * @param method a method to invoke
     */
    public void setMethod(@Nullable Method method) {
        this.method = method;
    }

    public Object @Nullable [] getMethodArgs() {
        return methodArgs;
    }

    /**
     * Set method parameters to invoke.
     *
     * If new method parameters are set on {@link ProxyDataSourceListener#beforeMethod(MethodExecutionContext)},
     * newly set parameters will be invoked.
     *
     * @param methodArgs method parameters
     */
    public void setMethodArgs(Object @Nullable [] methodArgs) {
        this.methodArgs = methodArgs;
    }

    public @Nullable Object getResult() {
        return result;
    }

    public void setResult(@Nullable Object result) {
        this.result = result;
    }

    public @Nullable Throwable getThrown() {
        return thrown;
    }

    public void setThrown(@Nullable Throwable thrown) {
        this.thrown = thrown;
    }

    /**
     * Returns the time taken to execute the method.
     *
     * <p>Measured with {@link System#nanoTime()}.
     *
     * @return elapsed time for the method execution
     */
    public Duration getElapsedTime() {
        return this.elapsedTime;
    }

    public void setElapsedTime(Duration elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    /**
     * @since 1.4.4
     */
    public @Nullable ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    /**
     * @since 1.4.4
     */
    public void setConnectionInfo(@Nullable ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public @Nullable ProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    public void setProxyConfig(@Nullable ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

    /**
     * Get the proxy object.
     *
     * @return proxy object
     * @since 1.10
     */
    public @Nullable Object getProxy() {
        return this.proxy;
    }

    public void setProxy(@Nullable Object proxy) {
        this.proxy = proxy;
    }

    /**
     * Store key/value pair.
     *
     * Mainly used for passing values between before and after listener callback.
     *
     * @param key   key
     * @param value value
     * @since 1.5.1
     */
    public void addCustomValue(String key, Object value) {
        this.customValues.put(key, value);
    }

    /**
     * @since 1.5.1
     */
    public <T> T getCustomValue(String key, Class<T> type) {
        return type.cast(this.customValues.get(key));
    }

}
