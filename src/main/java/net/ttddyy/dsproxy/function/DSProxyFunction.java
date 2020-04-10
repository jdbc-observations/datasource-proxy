package net.ttddyy.dsproxy.function;

/**
 * datasource-proxy version of {@link java.util.function.Function}.
 *
 * Created for retrolambda.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public interface DSProxyFunction<T, R> {

    R apply(T t);

}
