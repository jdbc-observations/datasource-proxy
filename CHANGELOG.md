# ChangeLog

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
