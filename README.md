# datasource-proxy

[![Build Status](https://travis-ci.org/ttddyy/datasource-proxy.svg?branch=master)](https://travis-ci.org/ttddyy/datasource-proxy)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.ttddyy/datasource-proxy/badge.svg)][maven-central_badge]
[![Download](https://api.bintray.com/packages/ttddyy/maven/datasource-proxy/images/download.svg) ](https://bintray.com/ttddyy/maven/datasource-proxy/_latestVersion)


## About

Provide proxy classes for JDBC API to intercept executing queries and methods.

### Versions

- 2.x _(Under development)_
  - ~~Java8 baseline.~~
  - `master` branch

- 1.x
  - Works with JDK1.6+ (works well with Java8 and above).
  - `1.x` branch

## User Guide

- [Current Release Version][user-guide-current]
- [Snapshot Version][user-guide-snapshot]
- [Older Version](https://github.com/ttddyy/datasource-proxy/wiki/User-Guide)

## Maven

```xml
<dependency>
  <groupId>net.ttddyy</groupId>
  <artifactId>datasource-proxy</artifactId>
  <version>[LATEST_VERSION]</version>
</dependency>
```

- latest version is: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.ttddyy/datasource-proxy/badge.svg)][maven-central_badge]
- No dependencies to other libraries, everything is optional.
    - For example, if you want to use slf4j logger with `SLF4JQueryLoggingListener`, then you need slf4j library.
- requires jdk1.6+

Snapshot is available via [oss sonatype snapshot repository](https://oss.sonatype.org/content/repositories/snapshots/net/ttddyy/datasource-proxy/).

## Related Projects

*Examples:*
- [datasource-proxy-examples][datasource-proxy-examples]

*Unit test support:*
- [datasource-assert][datasource-assert]


## Javadoc

- [Current Release Version][javadoc-current]
- [Snapshot Version][javadoc-snapshot]
- [Older Version](https://github.com/ttddyy/datasource-proxy/wiki/Javadoc)


----

[maven-central_badge]: https://maven-badges.herokuapp.com/maven-central/net.ttddyy/datasource-proxy/
[user-guide-current]: http://ttddyy.github.io/datasource-proxy/docs/current/user-guide/
[user-guide-snapshot]: http://ttddyy.github.io/datasource-proxy/docs/snapshot/user-guide/
[javadoc-current]: http://ttddyy.github.io/datasource-proxy/docs/current/api/
[javadoc-snapshot]: http://ttddyy.github.io/datasource-proxy/docs/snapshot/api/
[datasource-proxy-examples]: https://github.com/ttddyy/datasource-proxy-examples
[datasource-assert]: https://github.com/ttddyy/datasource-assert
