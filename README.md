# datasource-proxy

[![Maven Central](https://img.shields.io/maven-central/v/net.ttddyy/datasource-proxy)][maven-central_badge]


## About

Provides proxy classes for the JDBC API, allowing you to intercept query execution and JDBC method calls.

## User Guide

- [Current Release Version][user-guide-current]
- [Snapshot Version][user-guide-snapshot]
- [Older Version](https://github.com/jdbc-observations/datasource-proxy/wiki/User-Guide)

## Maven

```xml
<dependency>
  <groupId>net.ttddyy</groupId>
  <artifactId>datasource-proxy</artifactId>
  <version>[LATEST_VERSION]</version>
</dependency>
```

- Latest version: [![Maven Central](https://img.shields.io/maven-central/v/net.ttddyy/datasource-proxy)][maven-central_badge]
- The library has no required third-party dependencies; integrations are optional.
  - For example, using `SLF4JQueryLoggingListener` requires the SLF4J library.
- Requires JDK 6 or later. Java 8 and later are supported.

Snapshot releases are available from Maven Central. See the [official documentation](https://central.sonatype.org/publish/publish-portal-snapshots/#consuming-snapshot-releases-for-your-project) for instructions on using them.


## Related Projects

**Examples:**
- [datasource-proxy-examples][datasource-proxy-examples]


## Javadoc

- [Current Release Version][javadoc-current]
- [Snapshot Version][javadoc-snapshot]
- [Older Version](https://github.com/jdbc-observations/datasource-proxy/wiki/Javadoc)


----

[maven-central_badge]: https://central.sonatype.com/artifact/net.ttddyy/datasource-proxy
[user-guide-current]: http://jdbc-observations.github.io/datasource-proxy/docs/current/user-guide/
[user-guide-snapshot]: http://jdbc-observations.github.io/datasource-proxy/docs/snapshot/user-guide/
[javadoc-current]: http://jdbc-observations.github.io/datasource-proxy/docs/current/api/
[javadoc-snapshot]: http://jdbc-observations.github.io/datasource-proxy/docs/snapshot/api/
[datasource-proxy-examples]: https://github.com/ttddyy/datasource-proxy-examples
