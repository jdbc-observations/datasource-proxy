package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;

/**
 * @author Tadaya Tsuyukubo
 */
public class LastQueryListener implements ProxyDataSourceListener {
    private ExecutionInfo beforeExecInfo;
    private ExecutionInfo afterExecInfo;

    @Override
    public void beforeQuery(ExecutionInfo execInfo) {
        beforeExecInfo = execInfo;
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo) {
        afterExecInfo = execInfo;
    }

    public ExecutionInfo getBeforeExecInfo() {
        return beforeExecInfo;
    }

    public ExecutionInfo getAfterExecInfo() {
        return afterExecInfo;
    }

}