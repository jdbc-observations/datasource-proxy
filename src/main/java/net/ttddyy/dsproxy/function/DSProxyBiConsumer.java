package net.ttddyy.dsproxy.function;

/**
 * datasource-proxy version of {@link java.util.function.BiConsumer}.
 *
 * Created for retrolambda.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public interface DSProxyBiConsumer<T, U> {

    void accept(T t, U u);

}
