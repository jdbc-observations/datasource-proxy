# Changelog


**Changelog is moved to [datasource-proxy User Guide(Current)](http://ttddyy.github.io/datasource-proxy/docs/current/user-guide/index.html#changelog).**
_[(Snapshot documentation is here)](http://ttddyy.github.io/datasource-proxy/docs/snapshot/user-guide/index.html#changelog)_

----


## 1.4.3

- QueryLoggingListeners(Commons, SLF4J, JUL) added overridable `loggingCondition` callback(boolean supplier) that
  simply decides whether to skip entire logging logic based on the current log level set on its logger.    
    e.g.: when `SLF4JQueryLoggingListener` writes SQL in DEBUG level, but the logger is set to INFO(more serious
          than DEBUG), then it will NOT perform logging logic including constructing log statement, etc.

- Proxying `ResultSet` is refactored to align how other proxies are managed.  
  Also, existing resultset-proxy is renamed to `RepeatableReadResultSetProxyLogic`.  
  As part of refactoring, `ResultSetProxyJdbcProxyFactory` is removed.  
  To enable proxying `ResultSet`, `ProxyDataSourceBuilder` now has `#proxyResultSet()` and `#repeatableReadResultSet()`
  methods.
  ```java
    // before
    builder.jdbcProxyFactory(new ResultSetProxyJdbcProxyFactory()).build();
    // new
    builder.repeatableReadResultSet().build();  // or
    builder.proxyResultSet(new RepeatableReadResultSetProxyFactory()).build();
  ```  

- `ProxyConfig` is added to represent all proxy related configurations _(datasource name, listeners, proxy factory, 
  connection id manager)_. All values on `InterceptorHolder` are moved to `ProxyConfig` and `InterceptorHolder` class
  is removed.
  
- `MethodExecutionListener` is added.  
  `MethodExecutionListener` is a new type of listener that intercepts JDBC API calls: 
  - `Connection`, `Statement`, `PreparedStatement`, `CallableStatement`: All methods
  - `ResultSet`: All methods when result set proxy is enabled. (`ProxyDataSourceBuilder#[proxyResultSet()|repeatableReadResultSet()]`)
  - `ProxyDataSource`: `getConnection()` method
  
  listeners can be registered via `ProxyDataSourceBuilder#methodListener()`.
  ```java
    builder.methodListener(myMethodListener).build();
  ```

- `ProxyDataSourceBuilder` has added `beforeMethod()`, `afterMethod()`, `beforeQuery()`, and `afterQuery()` methods.  
  These methods help inlining listener definitions especially with Java8 Lambda expression.
  
  ```java
    ProxyDataSourceBuilder
      .create(actualDataSource)
      .name("MyDS")
      .proxyResultSet()  // apply listener on resultset
      // register MethodExecutionListener
      .afterMethod(executionContext -> {
          Method method = executionContext.getMethod();
          Class<?> targetClass = executionContext.getTarget().getClass();
          System.out.println(targetClass.getSimpleName() + "#" + method.getName());
      })
      // register QueryExecutionListener
      .afterQuery((execInfo, queryInfoList) -> {
          System.out.println("Query took " + execInfo.getElapsedTime() + "msec");
      })
      .build();
  ```
  _sample output:_
  ```sql
    # code:
    Connection conn = ds.getConnection();
    PreparedStatement ps = conn.prepareStatement("INSERT INTO users (id, name) VALUES (?, ?)");
    ps.setString(2, "FOO");
    ps.setInt(1, 3);
    ps.addBatch();
    ps.setInt(1, 4);
    ps.setString(2, "BAR");
    ps.addBatch();
    ps.executeBatch();
    ps.close();
    conn.close();
  
    # output:
    ProxyDataSource#getConnection
    JDBCConnection#prepareStatement
    JDBCPreparedStatement#setString
    JDBCPreparedStatement#setInt
    JDBCPreparedStatement#addBatch
    JDBCPreparedStatement#setInt
    JDBCPreparedStatement#setString
    JDBCPreparedStatement#addBatch
    JDBCPreparedStatement#executeBatch
    Query took 1msec
    JDBCPreparedStatement#close
    JDBCConnection#close
  ```
  


## 1.4.2

- Assign connection ID on each connection  
  When a connection is obtained from DataSource(`DataSource.getConnection()`), sequentially increasing unique number 
  is assigned as its connection ID. (default implementation: `DefaultConnectionIdManager`)  
  The connection ID is printed as `Connection` in logging.

- Remove methods that don't take `dataSourceName` on `JdbcProxyFactory`  
  Instead, you need to specify `null`, empty String, or datasource name to the `dataSourceName` parameter.
  Following methods are removed:
  - `Connection createConnection(Connection connection, InterceptorHolder interceptorHolder);`
  - `Statement createStatement(Statement statement, InterceptorHolder interceptorHolder);`
  - `PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query, InterceptorHolder interceptorHolder);`

- `DataSourceQueryCountListener` now takes a strategy to resolve `QueryCount`.  
  Default uses `ThreadQueryCountHolder` that uses thread local to hold `QueryCount`. This behaves same as before that 
  the `QueryCount` holds per request counts(servlet request-response lifecycle).  
  `SingleQueryCountHolder` uses single instance to hold count values. Therefore, this holds total accumulated 
  values from all threads.

- Update `SlowQueryListener` to use daemon threads as default.
  It is configurable by `SlowQueryListener#setUseDaemonThread` method.


## 1.4.1

- Add `setLog`/`setLogger` to `{Commons|SLF4J|JUL}QueryLoggingListener` to allow users to set custom logger.  
  Also added getters as well.

- Update `~QueryCountLoggingServletFilter` to allow configuring logger by name

- Add query count logging implementation for JUL(Java Util Logging)
  - `JULQueryCountLoggingHandlerInterceptor`
  - `JULQueryCountLoggingRequestListener`
  - `JULQueryCountLoggingServletFilter`
  
- Fix writing log with `null` in parameter set methods. (e.g: `setString(1, null);` )

- Add `SlowQueryListener` that triggers callback method when query takes longer than specified threshold time.  
  Also, added slow query logging listeners:
  - `CommonsSlowQueryListener`
  - `JULSlowQueryListener`
  - `SLF4JSlowQueryListener`
  - `SystemOutSlowQueryListener`
  
  In `ProxyDataSourceBuilder`, these methods are added:
  - `logSlowQueryByCommons()`
  - `logSlowQueryByJUL()`
  - `logSlowQueryBySlf4j()`
  - `logSlowQueryToSysOut()`
  
- Add support to easily apply formatters on each query for logging.  
  `DefaultQueryLogEntryCreator#formatQuery()` method has added.  
  Subclass can override this method to provides formatted query.  

  Example with `BasicFormatterImpl` in Hibernate.
  ```java
    // set this instance to logging listeners
    public class PrettyQueryEntryCreator extends DefaultQueryLogEntryCreator {
      private Formatter formatter = FormatStyle.BASIC.getFormatter();  // from hibernate
          
      @Override
      protected String formatQuery(String query) {
        return this.formatter.format(query);
      }
    }
  ```

- Add multiline output support for query logging.  
  `DefaultQueryLogEntryCreator` now has `setMultiline()` method, and `ProxyDataSourceBuilder` also has added 
  `multiline()` method.     
  When multiline is enabled, logged query entries become multi lined.  

  sample log output:
  ```
    Name:MyDS, Time:0, Success:True
    Type:Prepared, Batch:True, QuerySize:1, BatchSize:2 
    Query:["INSERT INTO users (id, name) VALUES (?, ?)"] 
    Params:[(1,foo),(2,bar)]
  ```

  set up with builder:
  ```java
    DataSource dataSource = 
        ProxyDataSourceBuilder
            .create(actualDataSource)
            .logQueryByCommons(INFO)
            .logSlowQueryByCommons(10, TimeUnit.MINUTES)
            .multiline()   // applies to both query logger and slow query logger
            .build();
  ```

- Deprecate `{Commons|SLF4J|JUL}QueryLoggingListener#resetLogger()` methods.  
  Use newly added `setLog(String)` or `setLogger(String)` method instead.


## 1.4

- Move logging related listeners to sub package
  - from `net.ttddyy.dsproxy.listener` to `net.ttddyy.dsproxy.listener.logging`

- classes for logging entry creation has been updated
  - `QueryLogEntryCreator#getLogEntryAsJson` has removed.
  - JSON style log entry creators is pulled up to `DefaultJsonQueryLogEntryCreator`
  - To use JSON style logging, you can set the `QueryLogEntryCreator` to `[Commons|SLF4J|JUL|SystemOut]QueryLoggingListener#setQueryLogEntryCreator()`
  - `OracleOutputParameterLogEntryCreator` has been split to `OutputParameterLogEntryCreator` and `OutputParameterJsonLogEntryCreator`

- `DefaultQueryLogEntryCreator#writeParamsForSingleEntry()` has split to `writeParamsEntryForSinglePreparedEntry()` and `writeParamsForSingleCallableEntry()`

- Do not include parameter index for logging prepared statement.

  Before(v1.3.3):

  ```
  ..., Params:[(1=10,2=foo),(1=20,2=bar)]
  ..., Params:[(1=30,2=FOO),(1=40,2=BAR)]
  ```

  ```json
  ..., "params":[{"1":"10","2":"foo"},{"1":"20","2":"bar"}]}
  ..., "params":[{"1":"30","2":"FOO"},{"1":"40","2":"BAR"}]}
  ```

  Now:

  ```
  ..., Params:[(10,foo),(20,bar)]
  ..., Params:[(30,FOO),(40,BAR)]
  ```

  ```json
  ..., "params":[["10","foo"],["20","bar"]]}
  ..., "params":[["30","FOO"],["40","BAR"]]}
  ```

- Add `JULQueryLoggingListener` which uses JUL(Java Utils Logging) to log executed queries

- Update logging for `setNull` and `registerOutParameter` to include sqltype
    e.g.: `NULL(VARCHAR)`, `OUTPUT(VARCHAR[12])`

- `ResultSetProxyJdbcProxyFactory` to create a proxy `ResultSet` that can be consumed more than once.
  Thanks _Liam Williams_ for this contribution!!

- `QueryExecutionListener` receives same instance of `ExecutionInfo` in `beforeQuery` and `afterQuery` methods


## 1.3.3

- update `DefaultQueryLogEntryCreator` to allow subclasses to override log entry details
 
## 1.3.2

- add `CommonsOracleOutputParameterLoggingListener`
- add new listener for oracle to log output params. `CommonsOracleOutputParameterLoggingListener`

## 1.3.1

- make logger name configurable in `CommonsQueryLoggingListener` and `SLF4JQueryLoggingListener`  
- `setNull` and `registerOutParameter` receives descriptive string value in `QueryInfo#getQueryArgsList` (temporal implementation)
- `ExecutionInfo` will have access to the statement/prepared/callable object used by the execution

## 1.3

- update minimum jdk to java6+
- add java8 new jdbc API (JDBC 4.2)
- new JNDI support class: `ProxyDataSourceObjectFactory`
- new fluent API builder: `ProxyDataSourceBuilder`

- logging:
  - update log format
  - add json format
  - more entries:  statement-type, batch, batch-size
  - new logger for System.Out

- change metric names:  
  call => total, elapsedTime => time, added success, failure, etc. 
- rename `~QueryCountLoggingFilter` to `~QueryCountServletFilter`
- remove deprecated methods

## 1.2.1

- fixed prepared statement getting already executed queries in listener ([Issue #9](https://github.com/ttddyy/datasource-proxy/issues/9))


## 1.2

- QueryTransformer and ParameterTransformer for query and parameter replacement
