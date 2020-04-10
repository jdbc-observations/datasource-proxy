package net.ttddyy.dsproxy.function;

/**
 * datasource-proxy version of {@link java.util.function.Consumer}.
 *
 * Created for retrolambda.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public interface DSProxyConsumer<T> {

    void accept(T t);

}
