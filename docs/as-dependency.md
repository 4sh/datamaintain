# How to - Install Datamaintain as a dependency

Datamaintain can be used as a dependency in your **Java** or **Kotlin server** : a handy solution to collaborate. 

## Available packages

| Package | Description | 
|---|---|
| datamaintain-core | Core package, needed for all uses of Datamaintain |
| datamaintain-driver-mongo | Mongo driver package to run scripts on a mongo database |
| datamaintain-driver-jdbc | JDBC driver package to run scripts on a database management system that has a JDBC driver |

## Add Datamaintain as a dependency

To install Datamaintain in your project, you will have to add it as a dependency. 
Since the releases are available on [jitpack](https://jitpack.io/), you will first have to add the jitpack 
repository in your project.
 
Then, you may add the dependencies to ```datamaintain-core``` and the driver module you need.

### Sample with gradle using kotlin DSL
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
    implementation("com.github.4sh.datamaintain:datamaintain-core:1.2.0"),
    implementation("com.github.4sh.datamaintain:datamaintain-driver-mongo:1.2.0")
} 
```
    
### Sample with gradle using groovy DSL
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
    implementation 'com.github.4sh.datamaintain:datamaintain-core:1.2.0',
    implementation 'com.github.4sh.datamaintain:datamaintain-driver-mongo:1.2.0',
} 
```
    
### Sample with maven

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
    <version>1.2.0</version>
</dependency>

<dependency>
    <groupId>com.github.4sh.datamaintain</groupId>
    <artifactId>datamaintain-driver-mongo</artifactId>
    <version>1.2.0</version>
</dependency>

```
