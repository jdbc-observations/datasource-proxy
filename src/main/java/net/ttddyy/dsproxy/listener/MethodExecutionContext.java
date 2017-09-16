package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.proxy.ProxyConfig;

import java.lang.reflect.Method;

/**
 * POJO to hold values for JDBC API invocations.
 *
 * @author Tadaya Tsuyukubo
 * @see MethodExecutionListener
 * @since 1.4.3
 */
public class MethodExecutionContext {

    public static class Builder {
        private Object target;
        private Method method;
        private Object[] methodArgs;
        private Object result;
        private Throwable thrown;
        private long elapsedTime;
        private ProxyConfig proxyConfig;

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
            context.proxyConfig = this.proxyConfig;
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

        public Builder elapsedTime(long elapsedTime) {
            this.elapsedTime = elapsedTime;
            return this;
        }

        public Builder proxyConfig(ProxyConfig proxyConfig) {
            this.proxyConfig = proxyConfig;
            return this;
        }
    }


    private Object target;
    private Method method;
    private Object[] methodArgs;
    private Object result;
    private Throwable thrown;
    private long elapsedTime;
    private ProxyConfig proxyConfig;

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getMethodArgs() {
        return methodArgs;
    }

    public void setMethodArgs(Object[] methodArgs) {
        this.methodArgs = methodArgs;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getThrown() {
        return thrown;
    }

    public void setThrown(Throwable thrown) {
        this.thrown = thrown;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    public void setProxyConfig(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }
}
