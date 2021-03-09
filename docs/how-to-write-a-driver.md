# Write a driver for Datamaintain

For now, these drivers are available:
* MongoDB

If you want to add a new driver, please start by checking the contribution guidelines.

## Create a new gradle module
Create a new gradle module, named ```driver-<DBMS name>``` with the following tree view:
```
.
├── build.gradle.kts
└── src
    ├── main
    │   └── kotlin
    │       └── datamaintain
    │           └── db
    │               └── driver
    │                   └── <DBMS-name>
    └── test
        └── kotlin
            └── datamaintain
                └── db
                    └── driver
                        └── <DBMS-name>
```

Your ```build.gradle.kts``` should contain at least this:

```kotlin
plugins {
    id("org.jetbrains.kotlin.jvm")
    maven // Needed for Jitpack
}

baseProject()

repositories {
    jcenter()
}

dependencies {
    compileOnly(project(":modules:core"))
    testImplementation(project(":modules:core"))
}
```
Add your module in the [root project settings](../settings.gradle.kts):
```kotlin
rootProject.name = "datamaintain"
   include(
           ...
           "modules:driver-<DBMS name>",
           ...
   )
```

## Implement the drivers interfaces

Your driver is what Datamaintain will use to communicate with your database management system (DBMS). To implement a driver, please implement the following classes:
* [DatamaintainDriver](../modules/core/src/main/kotlin/datamaintain/core/db/driver/DatamaintainDriver.kt)
* [DatamaintainDriverConfig](../modules/core/src/main/kotlin/datamaintain/core/db/driver/DatamaintainDriverConfig.kt)

**Make sure to add unit tests for every single function you write. If you don't, it will be requested in your pull request and your work will not be merged until you write them.**

## Add a sample

To test your sample in real conditions and help Datamaintain users understand how to use your driver, a sample is needed. Please write a sample in Java or Kotlin, at least one of the two. 

Create a folder in the [samples folder](../samples), with a name containing the language your sample is written in (Java or Kotlin)
 and the name of your DBMS. Please base your sample architecture on the [mongo java sample](../samples/java-mongo). If you lack ideas on relevant scripts to include in your sample, feel free to write scripts that do the same operations as the ones in the mongo java sample. You may find a brief description of what they do in the [mongo java sample README](../samples/java-mongo/README.md).

Once you have created your sample, the tree view of the samples should look like that:

```
.
├── ...
└── (java|kotlin)-<DBMS name>
    ├── build.gradle.kts
    └── src
        ├── main
        │   └── (java|kotlin)
        │       └── datamaintain
        │           └── samples
        │               └── (Java|Kotlin)<DMBSName>SampleMain.(java|kt)
        └── resources
            ├── config
            │   └── datamaintain.properties
            └── scripts    
                    ├── 1_script1
                    ├── 2_script2
                    └── 3_script3
```

Your ```build.gradle.kts``` should contain at least:

```kotlin
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.sourcemuse.mongo")
    maven // Needed for Jitpack
}

baseProject()

repositories {
    jcenter()
}

dependencies {
    implementation(project(":modules:core"))
    implementation(project(":modules:driver-<DMBS name>"))
}
```

Your ```datamaintain.properties``` should contain at least:

```
scan.path=samples/(java|kotlin)-<DMBS name>/src/main/resources/scripts
scan.identifier.regex=(.*?)_.*
```

If you are using Java, your main class should look like that:

```java
public class Java<DBMSName>SampleMain {
       public static void main(String[] args) throws IOException {
           // Retrieve datamaintain properties
           final Properties properties = new Properties();
           properties.load(JavaMongoSampleMain.class.getResourceAsStream("/config/datamaintain.properties"));
   
           // Instantiate DMBS name driver config
           final DatamaintainDriverConfig datamaintainDriverConfig = <DBMSName>DriverConfig.buildConfig(properties);
   
           // Instantiate datamaintain config
           final DatamaintainConfig config = DatamaintainConfig.buildConfig(datamaintainDriverConfig, properties);
   
           // Launch database update
           new Datamaintain(config).updateDatabase();
       }
}
```
## Update the documentation

Congratulations, your driver is ready for everyone to start using it! Now, tell the world that is has come to existence, you have to update the following files:

- The guide to write a driver (this file): add your driver in the [list of available drivers](how-to-write-a-driver.md#write-a-driver-for-datamaintain).
- The project README: add the package of your driver in the [list of available packages](../README.md#available-packages) and write a little description about it and add documentation about your driver configuration in the [Datatamaintain configuration section](../README.md#datamaintain-configuration).