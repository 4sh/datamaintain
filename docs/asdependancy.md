# How to - Install Datamaintain as a dependancy

Datamaintain can be used as a dependancy in your **Java** or **Kotlin server** : a handy solution to collaborate. 

## Available packages

| Package | Description | 
|---|---|
| datamaintain-core | Core package, needed for all uses of Datamaintain |
| datamaintain-mongo | Mongo driver package to run scripts on a mongo database |

## Add Datamaintain as a dependency

To install Datamaintain in your project, you will have to add it as a dependency. Since the releases are available on [jitpack](https://jitpack.io/), you will first have to add the jitpack repository in your project.
 
Then, you may add the dependencies to ```datamaintain-core``` and the driver module you need. A list of all the available modules is available [here](../README.md#available-packages). Here is an example of the dependencies declaration for a project using mongo:

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
