package net.ttddyy.dsproxy.listener;

import java.lang.reflect.Method;
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
    public void beforeMethod(Object target, Method method, Object[] args) {
        for (MethodExecutionListener listener : this.listeners) {
            listener.beforeMethod(target, method, args);
        }
    }

    @Override
    public void afterMethod(Object target, Method method, Object[] args, Object result, Throwable thrown, long elapsedTime) {
        for (MethodExecutionListener listener : this.listeners) {
            listener.afterMethod(target, method, args, result, thrown, elapsedTime);
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
