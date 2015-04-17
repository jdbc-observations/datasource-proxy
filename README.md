
[![Build Status](https://travis-ci.org/ttddyy/datasource-proxy.svg?branch=master)](https://travis-ci.org/ttddyy/datasource-proxy)
[ LATEST VERSION = 1.3 ] 

# datasource-proxy

## about

Provide proxy classes for JDBC API to intercept executing queries.

## feature

- Query and Parameter Logging  
  You can log executing database queries and parameters with choice of your logging framework(commons, slf4j, sysout).
  Output can be formatted as JSON.

- Query Metrics  
  You can collect statistics of executed query such as total time, number of select, insert, update, delete queries, etc.
  Output can be formatted as JSON.

- Custom Logic Injection for Query Execution  
  You can write own `QueryExecutionListener` and they can get called before/after query execution. 

- Web Application Support  
  `ProxyDataSource` can be configured via JNDI.   
  Query metrics information are available per web request basis(request-response lifecycle).  
  For UI with JSP, custom tag to access metrics(`<dsp:metrics/>`) is available as well.
   
- Query and parameter replacement
  `QueryTransformer` and `ParameterTransformer` allows you to modify executing query and parameters right before 
  calling the database.


## log example


Query execution:

```sql
Name:MyProxy, Time:1, Success:True, Type:Statement, Batch:False, QuerySize:1, BatchSize:0, Query:["CREATE TABLE users(id INT, name VARCHAR(255))"], Params:[]
Name:MyProxy, Time:1, Success:True, Type:Prepared, Batch:True, QuerySize:1, BatchSize:2, Query:["INSERT INTO users (id, name) VALUES (?, ?)"], Params:[(1=1,2=foo),(1=2,2=bar)]
```

```json
// JSON output
{"name":"MyProxy", "time":1, "success":true, "type":"Statement", "batch":false, "querySize":1, "batchSize":0, "query":["CREATE TABLE users(id INT, name VARCHAR(255))"], "params":[]}
{"name":"MyProxy", "time":0, "success":true, "type":"Prepared", "batch":true, "querySize":1, "batchSize":3, "query":["INSERT INTO users (id, name) VALUES (?, ?)"], "params":[{"1":"1","2":"foo"},{"1":"2","2":"bar"},{"1":"3","2":"baz"}]}
```

Query metrics:

```sql
Name:"MyProxy", Time:6, Total:1, Success:1, Failure:0, Select:1, Insert:0, Update:0, Delete:0, Other:0
```

```json
// JSON output
{"name":"MyProxy", "time":10, "total":3, "success":3, "failure":0, "select":1, "insert":2, "update":0, "delete":0, "other":0}
```


## maven

```xml
<dependency>
  <groupId>net.ttddyy</groupId>
  <artifactId>datasource-proxy</artifactId>
  <version>1.3</version>
</dependency>
```

- No dependencies to other libraries, everything is optional.
    - For example, if you want to use slf4j logger with `SLF4JQueryLoggingListener`, then you need slf4j library.

- requires jdk1.6+


### download

Available in [maven central repo](http://search.maven.org/#search|ga|1|datasource-proxy).


## how to use

Create `ProxyDataSource` class and pass it as a `DataSource` to your application.
 
**Java based**  

```java
DataSource dataSource = 
    ProxyDataSourceBuilder
        .create(actualDataSource)
        .logQueryByCommons(INFO)    // logQueryBySlf4j(), logQueryToSysOut()
        .countQuery()
        .build();
```

**Spring (XML)**  

```xml
<bean id="dataSource" class="net.ttddyy.dsproxy.support.ProxyDataSource">
  <property name="dataSource" ref="[ACTUAL DATASOURCE BEAN]"/>
  <property name="listener" ref="listeners"/>
</bean>

<bean id="listeners" class="net.ttddyy.dsproxy.listener.ChainListener">
  <property name="listeners">
    <list>
      <bean class="net.ttddyy.dsproxy.listener.CommonsQueryLoggingListener">
      <!-- <bean class="net.ttddyy.dsproxy.listener.SLF4JQueryLoggingListener"> -->
        <property name="logLevel" value="INFO"/> <!-- Default is DEBUG -->
      </bean>
      <bean class="net.ttddyy.dsproxy.listener.DataSourceQueryCountListener"/>
    </list>
  </property>
</bean>
```

**JNDI**  

```xml
<Resource name="jdbc/global/myProxy" 
          auth="Container"
          type="net.ttddyy.dsproxy.support.ProxyDataSource"
          factory="net.ttddyy.dsproxy.support.jndi.ProxyDataSourceObjectFactory"
          description="ds"
          listeners="commons,count"
          proxyName="MyProxy"
          dataSource="[REFERENCE_TO_ACTUAL_DATASOURCE_RESOURCE]"  <!-- ex: java:jdbc/global/myDS --> 
/>
```

**(optional)** To log query metrics(not query execution), add one of the followings to your application:

**By Servlet Filter (`javax.servlet.Filter`):**  
- `CommonsQueryCountLoggingServletFilter` 
- `SLF4JQueryCountLoggingServletFilter` 
- `SystemOutQueryCountLoggingServletFilter` 

**By Servlet Request Listener (`javax.servlet.ServletRequestListener`):**
- `CommonsQueryCountLoggingRequestListener`
- `SLF4JQueryCountLoggingRequestListener`
  
**By Spring HandlerInterceptor (`org.springframework.web.servlet.HandlerInterceptor`):**
- `CommonsQueryCountLoggingHandlerInterceptor`
- `SLF4JQueryCountLoggingHandlerInterceptor`
- `SystemOutQueryCountLoggingHandlerInterceptor`




## taglib (optional)

```jsp
<%@ taglib prefix="dsp" uri="http://www.ttddyy.net/dsproxy/tags" %>

<dsp:metrics metric="select"/>  - Select
<dsp:metrics metric="update" dataSource="FOO" />  - Num of update queries for datasource FOO
<dsp:metrics metric="total"/>  - Total Queries
<dsp:metrics metric="call"/>  - Num of DB Call
<dsp:metrics metric="time"/>  - Total TIme
```


## examples

Example projects: https://github.com/ttddyy/datasource-proxy-examples


---

# docs

- [Getting Started](https://github.com/ttddyy/datasource-proxy/wiki/Getting-Started)
- [Detail](https://github.com/ttddyy/datasource-proxy/wiki/Detail)
- [Configuration](https://github.com/ttddyy/datasource-proxy/wiki/Configuration)
- [How To Guide](https://github.com/ttddyy/datasource-proxy/wiki/How-To-Guide)

- [Documentation] (https://github.com/ttddyy/datasource-proxy/wiki/_pages)
- **[Javadoc (API)](https://github.com/ttddyy/datasource-proxy/wiki/Javadoc)**
- [Change Log](./CHANGELOG.md)
