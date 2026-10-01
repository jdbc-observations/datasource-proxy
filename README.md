# datasource-proxy

[![Maven Central](https://img.shields.io/maven-central/v/net.ttddyy/datasource-proxy)][maven-central_badge]


## About

Provide proxy classes for JDBC API to intercept executing queries and methods.

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

- latest version is: [![Maven Central](https://img.shields.io/maven-central/v/net.ttddyy/datasource-proxy)][maven-central_badge]
- No dependencies to other libraries, everything is optional.
    - For example, if you want to use slf4j logger with `SLF4JQueryLoggingListener`, then you need slf4j library.
- requires jdk1.6+ (works well with Java8 and above).

Snapshots are available via the Maven Central repository.
For instructions on how to consume snapshot releases, please refer to [the official documentation](https://central.sonatype.org/publish/publish-portal-snapshots/#consuming-snapshot-releases-for-your-project).


## Related Projects

*Examples:*
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
