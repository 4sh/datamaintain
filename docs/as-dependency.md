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

## Use Datamaintain in your code
You can take a look at the [java sample](../samples) for an example of how to create and start Datamaintain.

To start a Datamaintain execution in your code, you need to create a `Datamaintain` instance. 
The `Datamaintain` class is the core class.
Two functions are available :
* `updateDatabase`: search and execute the db scripts. Same has the `update-db` command on the cli.
* `listExecutedScripts`: return a `Sequence` with all scripts known by Datamaintain. Same as the `list` command on the cli.

Datamaintain's constructor takes a `DatamaintainDriverConfig`. There are two `Config objects`:
* A `DatamaintainDriverConfig` implementation (MongoDriverConfig, JdbcDriverConfig, ...) containing the database configuration
* `DatamaintainConfig` contains all datamaintain configuration, including the `DatamaintainDriverConfig`

You can create `Config objects` with properties, constructor or builder.

### Create Datamaintain Config Object
#### Properties
You can set all Datamaintain configuration in a property file: see [properties documentation](configuration.md).
From your code you can load the property file to create the `Config objects`. Each `Config` class has a `buildConfig` method for that:

```properties
scan.path=/scripts
scan.identifier.regex=(.*?)_.*
db.uri=mongodb://localhost:27017/datamaintain
```
```kotlin
val properties = Properties()
properties.load(MyClass::class.java.getResourceAsStream("datamaintain.properties"))

val driverConfig: DatamaintainDriverConfig = DatamaintainConfig.buildConfig(properties)
val config = DatamaintainConfig.buildConfig(driverConfig, properties)
```

#### Kotlin
You can create `Config objects` directly from constructor in pure kotlin:

```kotlin
val driverConfig: DatamaintainDriverConfig = MongoDriverConfig(uri = "mongodb://localhost:27017/datamaintain")
val config = DatamaintainConfig(
    path = Paths.get("/scripts"),
    identifierRegex = "(.*?)_.*",
    driverConfig = driverConfig
)
```

#### Java
Like Kotlin, you can create object directly from constructor, but in Java you cannot use Kotlin default parameters. 
It is difficult to create and maintain a Java code that uses the constructor. 
For Java user, Datamaintain provides a `Builder` class in each `Config` class:

```java
public class MyClass {
    final DatamaintainDriverConfig driverConfig = new MongoDriverConfig.Builder()
            .withUri("mongodb://localhost:27017/datamaintain-sample-java-mongo")
            .build();

    final DatamaintainConfig config = new DatamaintainConfig.Builder()
            .withPath(Paths.get("/scripts"))
            .withIdentifierRegex(new Regex("(.*?)_.*"))
            .withDriverConfig(driverConfig)
            .build();
}
```

### Create and use the Datamaintain object
You can create the `Datamaintain` object from a `DatamaintainConfig` and then call the list or update method :

```kotlin
val driverConfig: DatamaintainDriverConfig = ...
val config = ...

Datamaintain(config).updateDatabase()
```

### Exception
All Datamaintain exceptions use a custom exception. There are 2 exceptions to know:

#### DatamaintainBaseException
All Datamaintain exceptions extend this class. In addition to the message field, a resolutionMessage field 
can help to resolve.

#### DatamaintainException
This class extends `DatamaintainBaseException`. It is thrown if an exception occurs in a step code.
This class add two fields :
* `step` that indicates the step that throw the exception (SCAN, FILTER, ...) 
* `report` an object containing the scripts scanned, filtered, etc
