# datasource-proxy

## about

Provide proxy classes for JDBC API to intercept executing queries.

## features

- [logging support](https://github.com/ttddyy/datasource-proxy/wiki/Feature#wiki-feature_1)
- [collect query metrics](https://github.com/ttddyy/datasource-proxy/wiki/Feature#wiki-feature_2)
- [web application support](https://github.com/ttddyy/datasource-proxy/wiki/Feature#wiki-feature_3)
- [spring-framework friendly](https://github.com/ttddyy/datasource-proxy/wiki/Feature#wiki-feature_4)
- [inject custom logic before/after query execution](https://github.com/ttddyy/datasource-proxy/wiki/Feature#wiki-feature_5)
- [query and parameter replacement (since 1.2)](https://github.com/ttddyy/datasource-proxy/wiki/Feature#wiki-feature_6)

## maven

```xml
<dependency>
  <groupId>net.ttddyy</groupId>
  <artifactId>datasource-proxy</artifactId>
  <version>1.2.1</version>
</dependency>
```

- No dependencies to other libraries, everything is optional.
    - For example, if you use SLF4JQueryLoggingListener, then you need slf4j library.


*[>> application setup sample](https://github.com/ttddyy/datasource-proxy/wiki/Application-Setup-Sample)*

*[>> What's New](https://github.com/ttddyy/datasource-proxy/wiki/What's-New)*


## docs

- [Features](https://github.com/ttddyy/datasource-proxy/wiki/Feature)

- [Sample Configuration](https://github.com/ttddyy/datasource-proxy/wiki/Application-Setup-Sample)

- [Detailed Configuration](https://github.com/ttddyy/datasource-proxy/wiki/Detailed-Configuration)

- [Documentation] (https://github.com/ttddyy/datasource-proxy/wiki/_pages)

- **[Javadoc (API)](https://github.com/ttddyy/datasource-proxy/wiki/Javadoc)**

- [What's New](https://github.com/ttddyy/datasource-proxy/wiki/What's-New)

## download

They are available in [maven central repo](http://search.maven.org/#search|ga|1|datasource-proxy).

---
# how to use

## spring

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

## programmatic

**DataSource**

```java
DataSource dataSource = (DataSource)((new InitialContext()).lookup("java:comp/env/ref/ds"));

ProxyDataSource proxyDS = new ProxyDataSource();
proxyDS.setDataSource(dataSource);
proxyDS.setListener(new CommonsQueryLoggingListener());
proxyDS.setDataSourceName("MyDataSource");
```


**DriverManager**

```java
Class.forName("org.hsqldb.jdbcDriver");
Connection realConnection = DriverManager.getConnection("jdbc:hsqldb:mem:aname");
Connection proxyConnection = JdbcProxyFactory.createConnection(realConnection, new CommonsQueryLoggingListener());
```


## taglib (optional)

```jsp
<%@ taglib prefix="dsp" uri="http://www.ttddyy.net/dsproxy/tags" %>

<dsp:metrics metric="select"/>  - Select
<dsp:metrics metric="update" dataSource="FOO" />  - Num of update queries for datasource FOO
<dsp:metrics metric="total"/>  - Total Queries
<dsp:metrics metric="call"/>  - Num of DB Call
<dsp:metrics metric="elapsedTime"/>  - Total TIme
```


# log output sample

## query statistics metrics

```sql
DataSource:MyDatasourceA ElapsedTime:13 Call:7 Query:7 (Select:3 Insert:2 Update:1 Delete:0 Other:1)
DataSource:MyDatasourceB ElapsedTime:1 Call:1 Query:1 (Select:1 Insert:0 Update:0 Delete:0 Other:0)
```


## query execution

```sql
Time:13, Num:1, Query:{[create table emp ( id integer primary key, name varchar(10) );][]}
Time:10, Num:1, Query:{[insert into emp ( id, name )values (?, ?);][1, foo]}
Time:1, Num:1, Query:{[select this_.id as id0_0_, this_.name as name0_0_, this_.value as value0_0_ from emp this_ where (this_.id=? and this_.name=?)][1,bar]}
```


---

# architecture

![architecture diaglam](https://docs.google.com/drawings/pub?id=1KLaKmlp02c3lyQN1a_xhfG98AteyTIIVKSlnQW-aqsg&w=640&h=480&nonsense=architecture.png "architecture diaglam")
