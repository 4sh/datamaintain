# Datamaintain
[![CircleCI](https://circleci.com/gh/4sh/datamaintain.svg?style=shield)](https://circleci.com/gh/4sh/datamaintain) ![GitHub](https://img.shields.io/github/license/4sh/datamaintain)

Datamaintain is a Kotlin library that runs your scripts on your database and tracks the scripts runned. You may integrate it directly in your Java or Kotlin server or you may use the CLI.

## Table of contents
1. [Getting started](README.md#how-to-include-datamaintain-in-my-project)

## Getting started
### As a dependency
#### gradle kotlin
```kotlin

```

#### gradle groovy
```groovy```

#### maven

Add the following in your pom:

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.4sh.datamaintain</groupId>
    <artifactId>datamaintain-core</artifactId>
    <version>v1.0.0-rc8</version>
</dependency>

<dependency>
    <groupId>com.github.4sh.datamaintain</groupId>
    <artifactId>datamaintain-mongo</artifactId>
    <version>v1.0.0-rc8</version>
</dependency>

```

### Using the CLI
