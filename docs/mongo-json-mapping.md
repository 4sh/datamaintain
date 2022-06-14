# Json mapping of mongo driver
The datamaintain mongo driver need to read and write the `executedScripts` collection. This collection
contains scripts executed by Datamaintain.

Datamaintain provides 3 modules for json mapping using Jackson, Kotlinx Serialization or Gson.
You can also use your own json mapping implementation.

## How to set up mongo mapping dependency?
### Add dependency
Add mongo driver mapping dependency to your dependency with `core` and `driver-mongo`.
Each mapping module depends on a framework and must be added to your dependency :

| Framework             | Datamaintain Package                            | Depends on                                       |
|-----------------------|-------------------------------------------------|--------------------------------------------------|
| Jackson               | datamaintain-driver-mongo-mapping-jackson       | com.fasterxml.jackson.core:jackson-databind      |
| Kotlinx Serialization | datamaintain-driver-mongo-mapping-serialization | org.jetbrains.kotlinx:kotlinx-serialization-json |
| Gson                  | datamaintain-driver-mongo-mapping-gson          | com.google.code.gson:gson                        |

```kotlin
dependencies {
    implementation("io.github.4sh.datamaintain:datamaintain-core:2.0.0-1"),
    implementation("io.github.4sh.datamaintain:datamaintain-driver-mongo:2.0.0-1")
    implementation("io.github.4sh.datamaintain:datamaintain-driver-mongo-mapping-serialization:2.0.0-1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:latest.integration")
} 
```

### Use a mapping module in Datamaintain
The `jsonMapper` field in `MongoDriverConfig` is used to specify the json mapping class.
If you load one of our mapping dependencies, you do not need to specify `jsonMapper` implementation :
```kotlin
// Do not fill json mapper. It will be loaded automatically
MongoDriver(
    mongoUri = mongoUri,
)
```

If you load two of our mapping dependencies you must specify `jsonMapper` :
```kotlin
MongoDriver(
    mongoUri = mongoUri,
    jsonMapper = SerializationMapper()
)
```

The `jsonMapper` can also be specified in the properties :
```
db.mongo.json.mapper=datamaintain.db.driver.mongo.serialization.SerializationMapper
```

## How to use my own custom implementation?
If the mapping module does not work for you, you can implement your own.

### Implementation
Implement the `JsonMapper` class :
```kotlin
package datamaintain.db.driver.mongo

class MyJsonMapper: JsonMapper {
    override fun <T> fromJson(json: String, clazz: Class<T>): T = ...
    override fun <T> toJson(aObject: Any): String = ...
}
```

The dependency must follow this rule:
* The serializer must accept unknown fields on JSON (compatibility purpose)

### Test
You can test your own implementation by adding the `datamainain-mongo-driver-mapping-test` dependency.
This module provides the class `JsonMapperTest` for testing an implementation:
```kotlin
import datamaintain.db.driver.mongo.MyJsonMapper

internal class MyJsonMapperTest: JsonMapperTest(MyJsonMapper())
```

### Use my implementation in Datamaintain
Fill the `jsonMapper` field in `MongoDriverConfig` :
```kotlin
MongoDriver(
    mongoUri = mongoUri,
    jsonMapper = MyScriptMapper()
)
```

The `jsonMapper` can be specified in the properties.
Your implementation must have a public constructor with no arguments.
```
db.mongo.json.mapper=datamaintain.db.driver.mongo.MyScriptMapper
```

## FAQ
### Why use a JSON framework and not the mongo driver ?
The mongo driver executes javascript files. Javascript files can only be executed with the mongo shell (`mongo` or `mongosh`).
The Datamaintain maintainers team does not want to add a complex dependency as mongo driver just to read and write a collection.

### Why not use a BSON framework ?
The mongo shell does not support it correctly, Jackson with BSON module will serialize a mongo id in JSON :
```
{_id: {$oid: "62a886bc8c0dd9e44f9e5a83"}}
```

This is not supported by the mongo shell :
```
$ mongosh --eval 'db.test.insert({_id: {$oid: "62a886bc8c0dd9e44f9e5a83"}})'
MongoBulkWriteError: $oid is not valid for storage.
```

### I have the error DatamaintainMongoJsonMapperMoreThanOneImplFoundException
You have two or more modules loaded in your path. You must choose a json mapper explicitly, see [Use module in Datamaintain](mongo-json-mapping.md#Use module in Datamaintain)
