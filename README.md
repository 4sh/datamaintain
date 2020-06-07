# Datamaintain
[![CircleCI](https://circleci.com/gh/4sh/datamaintain.svg?style=shield)](https://circleci.com/gh/4sh/datamaintain) ![GitHub](https://img.shields.io/github/license/4sh/datamaintain)

Datamaintain is a Kotlin library that runs your scripts on your database and tracks the scripts runned. You may integrate it directly in your Java or Kotlin server or you may use the CLI.

## Table of contents
1. [Introduction](README.md#introduction)

## Introduction

During a project lifetime, you will often have to run scripts to update your database scheme or even add some data in it. The hard part comes when you have to ensure that all your scripts were executed and in the right order, which is exactly what Datamaintain is for! 

Each time your launch your server, Datamaintain will check if you added new scripts and if you did, play them in an order based on their identifier, which you may define. Every script execution will be remembered to prevent scripts from being run twice.

## Available packages

| Package | Description | 
|---|---|
| datamaintain-core | Core package, needed for all uses of Datamaintain |
| datamaintain-mongo | Mongo driver package to run scripts on a mongo database |

## Add Datamaintain as a dependency

To install Datamaintain in your project, you will have to add it as a dependency. Since the releases are available on [jitpack](https://jitpack.io/), you will first have to add the jitpack repository in your project.
 
Then, you may add the dependencies to ```datamaintain-core``` and the driver module you need. A list of all the available modules is available [here](README.md#available-packages). Here is an example of the dependencies declaration for a project using mongo:

- gradle using kotlin DSL:
    - In your root build.gradle, at the end of repositories:
    ```kotlin
    
    maven(url = "https://jitpack.io")
	```
    
    It should look like that:
    ```kotlin
    
    allprojects {
		repositories {
			...
    		maven(url = "https://jitpack.io")
		}
	}
    ```
    - Add the following dependency in your build.gradle:
    ```kotlin
    
    dependencies {
		implementation("com.github.4sh.datamaintain:datamaintain-core:v1.0.0-rc8"),
        	implementation("com.github.4sh.datamaintain:datamaintain-mongo:v1.0.0-rc8")
	} 
    ```
    
- gradle using groovy DSL: 
    - In your root build.gradle, at the end of repositories:
    ```groovy
    
    maven { url 'https://jitpack.io' }
	```
    
    It should look like that:
    ```groovy
    
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
    ```
    - Add the following dependency in your build.gradle:
    
    ```groovy
    
    dependencies {
	    implementation 'com.github.4sh.datamaintain:datamaintain-core:v1.0.0-rc8',
        implementation 'com.github.4sh.datamaintain:datamaintain-mongo:v1.0.0-rc8',
	} 
    ```
    
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

## Datamaintain configuration

### Core configuration

| Key | Description | Default value | Mandatory? | Values examples |
|---|---|---|---|---|
| scan.path | des | def | yes | exa |
| scan.path | des | def | yes | exa |
| scan.path | des | def | yes | exa |
| scan.path | des | def | yes | exa |
| scan.path | des | def | yes | exa |
| scan.path | des | def | yes | exa |
| scan.path | des | def | yes | exa |

### Mongo driver configuration

| Key | Description | Default value | Mandatory? | Values examples |
|---|---|---|---|---|
| scan.path | des | def | yes | exa |
| scan.path | des | def | yes | exa |
| scan.path | des | def | yes | exa |
| scan.path | des | def | yes | exa |
| scan.path | des | def | yes | exa |

## Use the CLI

You will find the 

![](docs/img/release-page-cli.png)

## Installation in a project with already executed scripts

When you already have executed scripts on your project and you want to start using Datamaintain, please follow those steps:
- Add Datamaintain as a dependency to your project, as described [here](README.md#installation). 
- Download the CLI from the version you are aiming for. The CLI is released as an asset in every Datamaintain release, you may find it in the [releases](https://github.com/4sh/datamaintain/releases).
- Execute the CLI using the following command replacing the arguments with the values you want. An explanation about each configuration key is provided here.
```bash

./datamaintain-cli --db-type $DB_TYPE --mongo-uri $MONGO_URI update-db --path $PATH --identifier-regex $REGEX --execution-mode FORCE_MARK_AS_EXECUTED
```

## Help to develop Datamaintain

## Contributors

