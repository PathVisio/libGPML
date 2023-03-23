# Development

Releases are created by the release manager and requires permission to submit the release to Maven Central
(using an approved Sonatype ([oss.sonatype.org](http://oss.sonatype.org/)) account). This document
explains how.

## Compiling

```shell
mvn clean install -Dgpg.skip
```

## Versioning

Instructions to increase the version to a development (ending with `-SNAPSHOT`) version:

```shell
mvn versions:set -DnewVersion=4.0.4-SNAPSHOT
```

Or to a release version:

```shell
mvn versions:set -DnewVersion=4.0.3
```

Also update the `../CITATION.cff` file for the version and release date.

## Making releases

Deploy to Sonatype (for access see below) with the following commands, for snapshots:

```shell
mvn clean deploy
```

And releases respectively:

```shell
mvn clean deploy -P release
```

### GitHub Release

Besides the Maven Central release, the next step is to make the release on GitHub
allowing Zenodo to archive the release which gives the DOI associated with the release.

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
