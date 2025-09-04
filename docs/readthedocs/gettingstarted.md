# Getting started
Welcome, this section is meant to give you some help getting hands on our project.
If you feel, something is missing, please contact us!

## Requirements

SimonaAPI requires Java to be at least Version 21.

## Where to get

Checkout latest from [GitHub](https://github.com/ie3-institute/simonaAPI) or use maven for dependency
management:

### Stable releases

On [Maven central](https://search.maven.org/artifact/com.github.ie3-institute/simonaAPI):

```xml
<dependency>
  <groupId>com.github.ie3-institute</groupId>
  <artifactId>simonaAPI</artifactId>
  <version>0.9.0</version>
</dependency>
```

### Snapshot releases

Available on [OSS Sonatype](https://s01.oss.sonatype.org/).
Add the correct repository:

```xml
<repositories>
  <repository>https://s01.oss.sonatype.org/content/repositories/snapshots</repository>
</repositories>
```

and add the dependency:

```xml
<dependency>
  <groupId>com.github.ie3-institute</groupId>
  <artifactId>simonaAPI</artifactId>
  <version>0.10-SNAPSHOT</version>
</dependency>
```

## Important changes

With the release of version `0.6.0` there were major changes in the way external simulations are
set up. Therefore, older simulations no longer work with the version `0.6.0`.

With the release of version `0.10.0` there was a lot of refactoring, which breaks compatibility with older simulations.
