package net.ttddyy.dsproxy;

import java.lang.reflect.Method;

/**
 * Contains query execution information.
 *
 * @author Tadaya Tsuyukubo
 */
public class ExecutionInfo {
    private String dataSourceName;
    private Method method;
    private Object[] methodArgs;
    private Object result;
    private long elapsedTime;
    private Throwable throwable;

    public ExecutionInfo() {
    }

    public ExecutionInfo(String dataSourceName, Method method, Object[] methodArgs) {
        this.dataSourceName = dataSourceName;
        this.method = method;
        this.methodArgs = methodArgs;
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

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
