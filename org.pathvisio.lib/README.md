# Development

Releases are created by the release manager and requires permission to submit the release to Maven Central
(using an approved Sonatype ([oss.sonatype.org](http://oss.sonatype.org/)) account). This document
explains how.

## Versioning

Instructions to increase the version to a development (ending with `-SNAPSHOT`) version:

```shell
mvn versions:set -DnewVersion=4.0.0-SNAPSHOT
```

Or to a release version:

```shell
mvn versions:set -DnewVersion=4.0.0
```

## Making releases

Deploy to Sonatype with the following commands, for snapshots:

```shell
mvn clean deploy
```

And releases respectively:

```shell
mvn clean deploy -P release
```

### Sonatype access

Deploying needs permission. Your account info is stored on Linux in `$HOME/.m2/settings.xml` and looks like:

```xml
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username></username>
      <password></password>
    </server>
  </servers>
</settings>
```
