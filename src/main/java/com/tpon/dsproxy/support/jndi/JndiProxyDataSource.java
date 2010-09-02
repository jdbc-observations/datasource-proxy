package com.tpon.dsproxy.support.jndi;

import com.tpon.dsproxy.ExecutionInfo;
import com.tpon.dsproxy.QueryInfo;
import com.tpon.dsproxy.listener.ChainListener;
import com.tpon.dsproxy.listener.QueryExecutionListener;
import com.tpon.dsproxy.support.ProxyDataSource;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Extends ProxyDataSource to be Referenceable and Serializable to support the usage inside JNDI.
 *
 * @author Juergen, 2010-03-08
 * @version 1.0
 */
public class JndiProxyDataSource extends ProxyDataSource implements Referenceable, Serializable {

    private static final long serialVersionUID = 5412567340724273513L;

    private DataSource parentDataSource;
    private QueryExecutionListener listener;

    public JndiProxyDataSource() {
    }

    public JndiProxyDataSource(String name, DataSource parentDataSource) {
        this.parentDataSource = parentDataSource;

        setDataSource(parentDataSource);
        setDataSourceName(name);
    }

    public QueryExecutionListener getListener() {
        return listener;
    }

    @Override
    public void setListener(QueryExecutionListener listener) {
        this.listener = listener instanceof Serializable ? listener : new SerializableListenerAdapter(listener);
        super.setListener(listener);
    }

    public DataSource getParentDataSource() {
        return parentDataSource;
    }

    /**
     * {@inheritDoc}
     */
    public Reference getReference() throws NamingException {
        return JndiProxyDataSourceFactory.createReference(this);
    }

    /**
     * Simple adapter supporting the serialization of trivial combined filters
     * using their no-args constructor.
     */
    public static class SerializableListenerAdapter implements Serializable, QueryExecutionListener {

        private static final long serialVersionUID = -5945569344502716192L;

        private String[] listenerClasses;
        private transient QueryExecutionListener listener;

        public SerializableListenerAdapter() {
        }

        public SerializableListenerAdapter(QueryExecutionListener listener) {
            if (listener == null)
                throw new NullPointerException();
            this.listener = listener;

            if (listener instanceof ChainListener) {
                ChainListener cl = (ChainListener) listener;
                List<String> classNames = new ArrayList<String>();
                for (QueryExecutionListener executionListener : cl.getListeners())
                    classNames.add(executionListener.getClass().getName());
                listenerClasses = classNames.toArray(new String[classNames.size()]);
            } else
                listenerClasses = new String[]{listener.getClass().getName()};
        }

        /**
         * {@inheritDoc}
         */
        public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
            if (listener == null)
                createListeners();
            listener.beforeQuery(execInfo, queryInfoList);
        }

        /**
         * {@inheritDoc}
         */
        public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
            if (listener == null)
                createListeners();
            listener.afterQuery(execInfo, queryInfoList);
        }

        private void createListeners() {
            try {
                if (listenerClasses.length == 1)
                    listener = (QueryExecutionListener) Class.forName(listenerClasses[0]).newInstance();
                else {
                    ChainListener cl = new ChainListener();
                    for (String s : listenerClasses)
                        cl.addListener((QueryExecutionListener) Class.forName(s).newInstance());
                    listener = cl;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
