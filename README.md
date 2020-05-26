# Datamaintain
[![CircleCI](https://circleci.com/gh/4sh/datamaintain.svg?style=shield)](https://circleci.com/gh/4sh/datamaintain) ![GitHub](https://img.shields.io/github/license/4sh/datamaintain)

Datamaintain is a Kotlin library that runs your scripts on your database and tracks the scripts runned. You may integrate it directly in your Java or Kotlin server or you may use the CLI.

## Table of contents
1. [Getting started](README.md#getting-started)

## Getting started

### Add Datamaintain as a dependency

To install Datamaintain in your project, you will have to add it as a dependency. Here are the ways to do it, depending on your dependency management software:
- gradle
- maven:

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

### Use the CLI

You will find the 

### Installation in a project with already executed scripts

When you already have executed scripts on your project and you want to start using Datamaintain, please follow those steps:
- Add Datamaintain as a dependency to your project, as described [here](README.md#installation). 
- Download the CLI from the version you are aiming for. The CLI is released as an asset in every Datamaintain release, you may find it in the [releases](https://github.com/4sh/datamaintain/releases).
- Execute the CLI using the following command replacing the arguments with the values you want. An explanation about each configuration key is provided here.
```bash

./datamaintain-cli --db-type $DB_TYPE --mongo-uri $MONGO_URI update-db --path $PATH --identifier-regex $REGEX --execution-mode FORCE_MARK_AS_EXECUTED
```

### As a dependency
#### gradle kotlin
```kotlin

```

#### gradle groovy
```groovy```

### Using the CLI
