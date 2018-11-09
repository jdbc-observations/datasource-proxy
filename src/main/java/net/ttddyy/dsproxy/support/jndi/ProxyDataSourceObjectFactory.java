package net.ttddyy.dsproxy.support.jndi;

import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * JNDI ObjectFactory to create proxy {@link DataSource}.
 *
 * <pre>{@code
 * <Resource name="jdbc/global/myProxy"
 *           auth="Container"
 *           type="javax.sql.DataSource"
 *           factory="net.ttddyy.dsproxy.support.jndi.ProxyDataSourceObjectFactory"
 *           description="ds"
 *           listeners="count,sysout,org.example.SampleListener"
 *           proxyName="DS-PROXY"
 *           format="json"
 *           dataSource="[REFERENCE_TO_ACTUAL_DATASOURCE_RESOURCE]"  <!-- ex: java:jdbc/global/myDS -->
 * />
 * }</pre>
 *
 * Parameters:
 * <ul>
 * <li> <b>dataSource <i>(required)</i></b>: Reference to actual datasource resource.  ex: java:jdbc/global/myDS
 * <li> <b>proxyName</b>:             ProxyDataSource name
 * <li> <b>listeners</b>:             Fully qualified class name of `ProxyDataSourceListener` implementation class,or predefined values below. Can be comma delimited.
 * <li> <b>queryTransformer</b>:      Fully qualified class name of `QueryTransformer` implementation class.
 * </ul>
 *
 * <i>listeners</i> parameter:
 * <ul>
 * <li> <b>count</b>:    alias to `net.ttddyy.dsproxy.listener.DataSourceQueryCountListener`
 * <li> <b>x.y.z.MyQueryExecutionListener</b>: Fully qualified class name of `ProxyDataSourceListener` implementation
 * </ul>
 *
 * <i>format</i> parameter:
 * <ul>
 * <li> <b>json</b>: set logging output format as JSON
 * </ul>
 *
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class ProxyDataSourceObjectFactory implements ObjectFactory {

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {

        if (obj == null || !(obj instanceof Reference)) {
            return null;
        }
        Reference reference = (Reference) obj;

        String dataSourceJndiName = getContentFromReference(reference, "dataSource");
        String proxyDataSourceName = getContentFromReference(reference, "proxyName");
        String listenerNames = getContentFromReference(reference, "listeners");
        String queryTransformer = getContentFromReference(reference, "queryTransformer");

        // retrieve datasource from JNDI
        Object dataSourceResource = new InitialContext().lookup(dataSourceJndiName);
        if (dataSourceResource == null) {
            throw new Exception(String.format("%s is not available.", dataSourceJndiName));
        } else if (!(dataSourceResource instanceof DataSource)) {
            throw new Exception(String.format("%s is not DataSource: %s", dataSourceJndiName, dataSourceResource));
        }
        DataSource dataSource = (DataSource) dataSourceResource;

        // builder
        ProxyDataSourceBuilder builder = ProxyDataSourceBuilder.create(dataSource);

        if (proxyDataSourceName != null) {
            builder.name(proxyDataSourceName);
        }

        if (listenerNames != null) {
            for (String listenerName : getListenerNames(listenerNames)) {
                if ("count".equalsIgnoreCase(listenerName)) {
                    builder.countQuery();
                } else {
                    ProxyDataSourceListener listener = createNewInstance(ProxyDataSourceListener.class, listenerName);
                    builder.listener(listener);
                }
            }
        }

        if (queryTransformer != null) {
            QueryTransformer transformer = createNewInstance(QueryTransformer.class, queryTransformer);
            builder.queryTransformer(transformer);
        }

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    protected <T> T createNewInstance(Class<T> clazz, String className) throws Exception {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (Exception e) {
            String msg = String.format("Failed to create %s - %s: %s", clazz.getSimpleName(), e.getClass().getName(), e.getMessage());
            throw new Exception(msg);
        }
    }

    protected String getContentFromReference(Reference reference, String key) {
        RefAddr refAddr = reference.get(key);
        if (refAddr == null) {
            return null;
        }
        return refAddr.getContent().toString();
    }

    protected String[] getListenerNames(String listenerNames) {
        Set<String> listenerNameSet = new HashSet<String>();
        for (String listenerName : listenerNames.split(",")) {
            listenerNameSet.add(trim(listenerName));
        }
        return listenerNameSet.toArray(new String[listenerNameSet.size()]);

    }

    protected String trim(String s) {
        int length = s.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (!Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
