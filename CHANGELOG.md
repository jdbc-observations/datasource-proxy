# ChangeLog

## 1.4

- Move logging related listeners to sub package
  - from `net.ttddyy.dsproxy.listener` to `net.ttddyy.dsproxy.listener.logging`

- classes for logging entry creation has been updated
  - `QueryLogEntryCreator#getLogEntryAsJson` has removed.
  - JSON style log entry creators is pulled up to `DefaultJsonQueryLogEntryCreator`
  - To use JSON style logging, you can set the `QueryLogEntryCreator` to `[Commons|SLF4J|JUL|SystemOut]QueryLoggingListener#setQueryLogEntryCreator()`
  - `OracleOutputParameterLogEntryCreator` has been split to `OutputParameterLogEntryCreator` and `OutputParameterJsonLogEntryCreator`

- `DefaultQueryLogEntryCreator#writeParamsForSingleEntry()` has split to `writeParamsEntryForSinglePreparedEntry()` and `writeParamsForSingleCallableEntry()`

- When logging prepared statement, do not include parameter index.

  Before(v1.3.3):

  ```
  ..., Params:[(1=1,2=foo),(1=2,2=bar)]
  ..., Params:[(1=3,2=FOO),(1=4,2=BAR)]
  ```

  ```json
  ..., "params":[{"1":"1","2":"foo"},{"1":"2","2":"bar"}]}
  ..., "params":[{"1":"3","2":"FOO"},{"1":"4","2":"BAR"}]}
  ```

  Now:

  ```
  ..., Params:[(1,foo),(2,bar)]
  ..., Params:[(3,FOO),(4,BAR)]
  ```

  ```json
  ..., "params":[["1","foo"],["2","bar"]]}
  ..., "params":[["3","FOO"],["4","BAR"]]}
  ```

- Add `JULQueryLoggingListener` which uses JUL(Java Utils Logging) to log executed queries

- Update logging for `setNull` and `registerOutParameter` to include sqltype
    e.g.: `NULL(VARCHAR)`, `OUTPUT(VARCHAR[12])`

- `ResultSetProxyJdbcProxyFactory` to create a proxy `ResultSet` that can be consumed more than once.  
  Thanks _Liam Williams_ for this contribution!!

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
