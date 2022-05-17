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

Then, you may add the dependencies to ```datamaintain-core``` and the driver module you need.

### Sample with gradle using kotlin DSL
- In your root build.gradle, make sure you declared maven central:
```kotlin
mavenCentral()
```

It should look like that:
```kotlin
allprojects {
    repositories {
        ...
        mavenCentral()
    }
}
```
- Add the following dependency in your build.gradle:
```kotlin
dependencies {
    implementation("io.github.4sh.datamaintain:datamaintain-core:2.0.0-1"),
    implementation("io.github.4sh.datamaintain:datamaintain-driver-mongo:2.0.0-1")
} 
```
    
### Sample with gradle using groovy DSL
- In your root build.gradle, make sure you declared maven central:
```groovy
mavenCentral()
```

It should look like that:
```groovy
allprojects {
    repositories {
        ...
        mavenCentral()
    }
}
```
- Add the following dependency in your build.gradle:

```groovy

dependencies {
    implementation 'io.github.4sh.datamaintain:datamaintain-core:2.0.0-1',
    implementation 'io.github.4sh.datamaintain:datamaintain-driver-mongo:2.0.0-1',
} 
```
    
### Sample with maven

```xml
<dependency>
    <groupId>io.github.4sh.datamaintain</groupId>
    <artifactId>datamaintain-core</artifactId>
    <version>2.0.0-1</version>
</dependency>

<dependency>
    <groupId>io.github.4sh.datamaintain</groupId>
    <artifactId>datamaintain-driver-mongo</artifactId>
    <version>2.0.0-1</version>
</dependency>

```
