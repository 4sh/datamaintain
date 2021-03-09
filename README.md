# Datamaintain
[![CircleCI](https://circleci.com/gh/4sh/datamaintain.svg?style=shield)](https://circleci.com/gh/4sh/datamaintain) ![GitHub](https://img.shields.io/github/license/4sh/datamaintain)

Datamaintain is a Kotlin library for maintaining consistency between data and code : it runs your scripts in your database and tracks the scripts runned. 

According to your needs you may :
- Use it as a dependancy in your Java or Kotlin server, really handy to collaborate,
- And/or use the Command Line Interface (CLI) to enable more control when deploying.

For the moment Datamaintains supports only the DBMS MongoDB, but other configurations will soon be added (JDBC very soon)! If you want to contribute, here is the documentation ["how to write a driver for your DBMS"](README.md#use-the-cli). It is quite easy (no need to deeply know Datamaintain) and it would help a lot 🙏.


## Benefits
During a project lifetime, you will often have to run scripts to update your database scheme or even add some data in it. The hard part comes when you have to ensure that all your scripts were executed and in the right order, which is exactly what Datamaintain is for! 

- **Transparent**, thanks to the script execution reports.
- **Light**, with few dependencies.
- **Customizable**, it is possible to run the scripts in different manners : with the server or independantly, ordered with tags, with your own program...
- **Idiot-proof**, Datamaintain has an identification based on the content that enables to manage homonyms scripts. You will never get angry with your colleagues again. We also have unit tests for the Datamaintain contributors. 
- **Evolutive and open-source**, the architecture does not matter to the DBMS so that we can easily create new DBMS drivers and finally have a common framework between all our projects.  We are a team of developers that come mainly from the [4SH agency](https://www.4sh.fr/), where Datamaintain is  used in several big projects. We works regularly on it according to the feedbacks of the users (our colleagues). We would be really happy to work with other people. If you are interested in this project don't hesitate to contact us.

## What is the best configuration for me ?

**👌 I want the simplest :** 

I embed it. The scripts are launched automatiquelly when the server starts. This is simple and really handy to collaborate. 

**👾 I want personalisation :** 
- Used as a dependancy : I can program it with Java.
- Used with the Command line interface : I can master the order of my scripts and launch them independantly to the server thanks to executable file. I can also use Datamaintain in a project with already executed scripts. 

**Can I use both the embed mode and the Command Line Interface?**
Yes ! And that is often the case ! You can develop your project with Datamaintain embedded and then deploy it in production with the Command Line Interface (CLI). 

## Quick links
### Getting started
- How to install Datamaintain as a dependacy, in Command Line Interface
- How to configure it
- Use cases : read script reports, use tags (ex. CIN), 
### Digging deeper
- How to enhance performances with a synthesis of the scripts ? 
### Contribute
- How does Datamaintain (what Datamaintains does for you)
- How to write a drive. 


[Available packages](README.md#available-packages)
[Add Datamaintain as a dependency](README.md#add-datamaintain-as-a-dependency)
[Datamaintain configuration](README.md#datamaintain-configuration)
[Use the CLI](README.md#use-the-cli)
[Installation in a project with already executed scripts](README.md#installation-in-a-project-with-already-executed-scripts)

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

## Executed scripts in your database
Your scripts executions will be stored in your database. In Mongo, you will have a collection named ```executedScripts``` that will contain executed scripts, as defined below:

| Attribute | Description |
|---|---|
| id | | 
| name | | 
| checksum | | 
| identifier | | 
| executionStatus | | 
| executionDurationInMillis | Duration of your script execution, in milliseconds. | 
| executionOutput | | 
