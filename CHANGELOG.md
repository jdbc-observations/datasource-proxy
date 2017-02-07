# ChangeLog

## 1.4.1 _(SNAPSHOT)_

- Add `setLog`/`setLogger` to `{Commons|SLF4J|JUL}QueryLoggingListener` to allow users to set custom logger.

- Update `~QueryCountLoggingServletFilter` to allow configuring logger by name

- Add query count logging implementation for JUL(Java Util Logging)
  - `JULQueryCountLoggingHandlerInterceptor`
  - `JULQueryCountLoggingRequestListener`
  - `JULQueryCountLoggingServletFilter`
  
- Fix writing log with `null` in parameter set methods. (e.g: `setString(1, null);` )


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
