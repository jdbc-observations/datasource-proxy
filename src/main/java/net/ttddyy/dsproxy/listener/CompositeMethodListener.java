package net.ttddyy.dsproxy.listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Execute chain of {@link MethodExecutionListener}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public class CompositeMethodListener implements MethodExecutionListener {
    private List<MethodExecutionListener> listeners = new ArrayList<MethodExecutionListener>();

    @Override
    public void beforeMethod(MethodExecutionContext executionContext) {
        for (MethodExecutionListener listener : this.listeners) {
            listener.beforeMethod(executionContext);
        }
    }

    @Override
    public void afterMethod(MethodExecutionContext executionContext) {
        for (MethodExecutionListener listener : this.listeners) {
            listener.afterMethod(executionContext);
        }
    }

    public boolean addListener(MethodExecutionListener listener) {
        return this.listeners.add(listener);
    }

    public List<MethodExecutionListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<MethodExecutionListener> listeners) {
        this.listeners = listeners;
    }
}
