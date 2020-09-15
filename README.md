# Datamaintain
[![CircleCI](https://circleci.com/gh/4sh/datamaintain.svg?style=shield)](https://circleci.com/gh/4sh/datamaintain) ![GitHub](https://img.shields.io/github/license/4sh/datamaintain)

Datamaintain is a Kotlin library that runs your scripts on your database and tracks the scripts runned. You may integrate it directly in your Java or Kotlin server or you may use the CLI.

## Table of contents
1. [Introduction](README.md#introduction)
2. [Available packages](README.md#available-packages)
3. [Add Datamaintain as a dependency](README.md#add-datamaintain-as-a-dependency)
4. [Datamaintain configuration](README.md#datamaintain-configuration)
5. [Use the CLI](README.md#use-the-cli)
6. [Installation in a project with already executed scripts](README.md#installation-in-a-project-with-already-executed-scripts)

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
		implementation("com.github.4sh.datamaintain:datamaintain-core:v1.0.0-rc14"),
		implementation("com.github.4sh.datamaintain:datamaintain-mongo:v1.0.0-rc14")
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
	    implementation 'com.github.4sh.datamaintain:datamaintain-core:v1.0.0-rc14',
        implementation 'com.github.4sh.datamaintain:datamaintain-mongo:v1.0.0-rc14',
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
    <version>v1.0.0-rc14</version>
</dependency>

<dependency>
    <groupId>com.github.4sh.datamaintain</groupId>
    <artifactId>datamaintain-mongo</artifactId>
    <version>v1.0.0-rc14</version>
</dependency>

```

## Datamaintain configuration

### Core configuration

| Key | Description | Default value | Mandatory? | Values examples |
|---|---|---|---|---|
| scan.path | Path to the folder containing all your scripts | ```./scripts/``` | yes |  |
| scan.identifier.regex | Regex that will be used to determine an identifier for each file. It has to contain a capturing group. Identifiers are then used to sort the scripts before running them. | ```(.*)``` (with this regex, the script's whole name will be its identifier) | no | With the regex ```(.*?)_.*```, a script named ```1.23_my-script.js``` will have ```1.23``` as its identifier  |
| scan.tags.createFromFolder | If true, scripts will have their parent folders names as tags. Relative path to ```scan.path``` is used.  | ```false``` | no | ```false``` or ```true``` |
| tag.*your_tag* | Glob paths to your scripts that you want to apply the tag "your_tag" on. To declare multiple tags, you will have to add multiple properties in your settings. A tag ```my_tag``` will have as as property name ```tag.my_tag``` **WARNING:** ALWAYS declare your tags using absolute paths. Relative paths and even using a tilde (~) won't do the trick. |  | no | ```[data/*, script1.js, old/old_script1.js]``` |
| filter.tags.whitelisted | Scripts that have these tags will be considered | None | no | ```DATA,tag``` |
| filter.tags.blacklisted | Scripts that have these tags will be ignored. A script having a whitelisted tag and a blacklisted tag will be ignored | None | no | ```DATA,tag``` |
| execution.mode | Execution mode. Possible values:<br />- ```NORMAL```: Regular execution: your scripts will be run on your database.<br />- ```DRY```: Scripts will not be executed. A full report of what would happen is you ran Datamaintain normally will be logged.<br />- ```FORCE_AS_EXECUTED```: Scripts will not be executed but their execution will be remembered by Datamaintain for later executions. | ```NORMAL``` | no | ```NORMAL```, ```DRY``` or ```FORCE_MARK_AS_EXECUTED``` |
| verbose | If true, more logs will be printed | ```false``` | no | ```true``` or ```false``` |
| prune.tags.to.run.again | Scripts that have these tags will be run, even they were already executed  | None | no | ```tag,again``` |
### Mongo driver configuration

| Key | Description | Default value | Mandatory? | Values examples |
|---|---|---|---|---|
| db.mongo.uri | Mongo URI to your mongo server. **Database name is mandatory.** Please see the [mongo URI documentation](https://docs.mongodb.com/manual/reference/connection-string/) to learn about writing mongo URIs. |  | yes | ```mongodb://localhost/my-db```<br />```mongodb://localhost:8000/my-db```<br />```mongodb://username:password@localhost/my-db```<br />```mongodb+srv://server.example.com/my-db``` <br />```mongodb://my-db,my-db2:27018/my-db``` <br /> |
| db.mongo.tmp.path | Path where the driver will write temporary files. | ```/tmp/datamaintain.tmp``` | no |  |
| db.mongo.client.path | Path or alias to your mongo executable. | ```mongo``` | no |  |
| db.mongo.print.output | If true, mongo output will be logged. | ```false``` | no | ```true``` or ```false``` |
| db.mongo.save.output | If true, mongo output will be saved in script execution report.  | ```false``` | no | ```true``` or ```false``` |

## Use the CLI

You will find the CLI for each release in its assets in the [releases](https://github.com/4sh/datamaintain/releases). To launch Datamaintain using the CLI, you just have to execute the bash script you will find in the archive. To give values to the settings, you just have to add ```--setting $SETTING_VALUE``` after ```./datamaintain-cli```.

![](docs/img/release-page-cli.png)

## Installation in a project with already executed scripts

When you already have executed scripts on your project and you want to start using Datamaintain, please follow those steps:
- Add Datamaintain as a dependency to your project, as described [here](README.md#installation). 
- Download the CLI from the version you are aiming for. The CLI is released as an asset in every Datamaintain release, you may find it in the [releases](https://github.com/4sh/datamaintain/releases).
- Execute the CLI using the following command replacing the arguments with the values you want. An explanation about each configuration key is provided here.
```bash

./datamaintain-cli --db-type $DB_TYPE --mongo-uri $MONGO_URI update-db --path $PATH --identifier-regex $REGEX --execution-mode FORCE_MARK_AS_EXECUTED
```
