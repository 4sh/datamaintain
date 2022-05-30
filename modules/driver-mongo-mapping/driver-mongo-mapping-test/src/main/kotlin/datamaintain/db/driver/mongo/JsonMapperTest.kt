package datamaintain.db.driver.mongo

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import datamaintain.core.script.ExecutionStatus
import datamaintain.core.script.ScriptAction
import datamaintain.db.driver.mongo.mapping.ExecutedScriptDb
import datamaintain.db.driver.mongo.mapping.LightExecutedScriptDb
import net.minidev.json.JSONArray
import net.minidev.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll


abstract class JsonMapperTest(private val jsonMapper: JsonMapper) {
    private val jsonPath = JsonPath.using(Configuration.builder()
        .options(Option.DEFAULT_PATH_LEAF_TO_NULL)  // Serializer can choose to write or not a null or empty field
        .build())

    @Test
    fun toJson() {
        // Given
        val executedScript = buildExecutedScriptDb()

        // When
        val serializeExecutedScript = jsonMapper.toJson(executedScript)

        // Then
        val executedScriptPath = jsonPath.parse(serializeExecutedScript)
        assertAll(
            { executedScriptPath.assertPathEqual("$._id", executedScript._id) },
            { executedScriptPath.assertPathEqual("$.name", "name") },
            { executedScriptPath.assertPathEqual("$.checksum", "de0f0c582eff74ec1b67ac065078dc7e") },
            { executedScriptPath.assertPathEqual("$.identifier", "test") },
            { executedScriptPath.assertPathEqual("$.executionStatus", ExecutionStatus.OK.name) },
            { executedScriptPath.assertPathEqual("$.action", ScriptAction.RUN.name) },
            { executedScriptPath.assertPathEqual("$.executionDurationInMillis", 1) },
            { executedScriptPath.assertPathEqual("$.executionOutput", "output") },
            { executedScriptPath.assertPathEqual("$.flags.length()", 2) },
            { executedScriptPath.assertPathEqual("$.flags[0]", "flag1") },
            { executedScriptPath.assertPathEqual("$.flags[1]", "flag2") },
            { executedScriptPath.assertPathEqual("$.length()", 9) }
        )
    }

    @Test
    fun toJsonWithNullField() {
        // Given
        val executedScript = buildExecutedScriptDb()
        executedScript.action = null
        executedScript.executionDurationInMillis = null
        executedScript.executionOutput = null

        // When
        val serializeExecutedScript = jsonMapper.toJson(executedScript)

        // Then
        val executedScriptPath = jsonPath.parse(serializeExecutedScript)
        assertAll(
            { executedScriptPath.assertPathEqual("$._id", executedScript._id) },
            { executedScriptPath.assertPathEqual("$.name", "name") },
            { executedScriptPath.assertPathEqual("$.checksum", "de0f0c582eff74ec1b67ac065078dc7e") },
            { executedScriptPath.assertPathEqual("$.identifier", "test") },
            { executedScriptPath.assertPathEqual("$.executionStatus", ExecutionStatus.OK.name) },
            { executedScriptPath.assertPathEqual("$.action", null) },
            { executedScriptPath.assertPathEqual("$.executionDurationInMillis", null) },
            { executedScriptPath.assertPathEqual("$.executionOutput", null) },
            { executedScriptPath.assertPathEqual("$.flags.length()", 2) },
            { executedScriptPath.assertPathEqual("$.flags[0]", "flag1") },
            { executedScriptPath.assertPathEqual("$.flags[1]", "flag2") },
            {
                val hasProperty = jsonHasProperty(serializeExecutedScript, "action")
                val expectedProperties = if (hasProperty) 9 else 6
                executedScriptPath.assertPathEqual("$.length()", expectedProperties)
            }
        )
    }

    @Test
    fun toJsonWithEmptyField() {
        // Given
        val executedScript = buildExecutedScriptDb()
        executedScript.flags = listOf()

        // When
        val serializeExecutedScript = jsonMapper.toJson(executedScript)

        // Then
        val executedScriptPath = jsonPath.parse(serializeExecutedScript)
        assertAll(
            { executedScriptPath.assertPathEqual("$._id", executedScript._id) },
            { executedScriptPath.assertPathEqual("$.name", "name") },
            { executedScriptPath.assertPathEqual("$.checksum", "de0f0c582eff74ec1b67ac065078dc7e") },
            { executedScriptPath.assertPathEqual("$.identifier", "test") },
            { executedScriptPath.assertPathEqual("$.executionStatus", ExecutionStatus.OK.name) },
            { executedScriptPath.assertPathEqual("$.action", ScriptAction.RUN.name) },
            { executedScriptPath.assertPathEqual("$.executionDurationInMillis", 1) },
            { executedScriptPath.assertPathEqual("$.executionOutput", "output") },
            { executedScriptPath.assertPathIsAnEmptyArrayOrNull("$.flags") },
            {
                val hasProperty = jsonHasProperty(serializeExecutedScript, "flags")
                val expectedProperties = if (hasProperty) 9 else 8
                executedScriptPath.assertPathEqual("$.length()", expectedProperties)
            }
        )
    }

    @Test
    fun fromJson() {
        // Given
        val json = buildJsonLightExecutedScriptArray().toJSONString()

        // When
        val lightExecutedScriptArray = jsonMapper.fromJson(json, Array<LightExecutedScriptDb>::class.java)

        //Then
        assert(lightExecutedScriptArray != null) { "fromJson function return null value over an array" }
        val lightExecutedScript = lightExecutedScriptArray!!.toList()
        assert(lightExecutedScript.size == 2) { "fromJson function return an array with size ${lightExecutedScript.size} but expected size was 2" }

        val firstLightExecutedScript = lightExecutedScript[0]
        val secondLightExecutedScript = lightExecutedScript[1]

        assertAll(
            { assertEquals(firstLightExecutedScript._id, "3b0b62bc-4937-4757-9d49-25730492ce9f", "results[0]._id") },
            { assertEquals(firstLightExecutedScript.name, "1-test.js", "results[0].name") },
            { assertEquals(firstLightExecutedScript.checksum, "5247ebe6d2788ce6bcd4fc989c4528c8", "results[0].checksum") },
            { assertEquals(firstLightExecutedScript.identifier, "1", "results[0].identifier") },

            { assertEquals(secondLightExecutedScript._id, "7a4eaf36-ec96-4d3d-8c64-6b23b6e42290", "results[1]._id") },
            { assertEquals(secondLightExecutedScript.name, "2-test.js", "results[1].name") },
            { assertEquals(secondLightExecutedScript.checksum, "96188ec04746d6d213cdf553b9ddb138", "results[1].checksum") },
            { assertEquals(secondLightExecutedScript.identifier, "2", "results[1].identifier") },
        )
    }

    @Test
    fun fromJsonWithUnknownField() {
        // Given
        val jsonArray = buildJsonLightExecutedScriptArray()

        // Add an extra field
        (jsonArray[0] as JSONObject).appendField("executionStatus", "OK")
        val json = jsonArray.toJSONString()

        // When
        val lightExecutedScriptArray = jsonMapper.fromJson(json, Array<LightExecutedScriptDb>::class.java)

        //Then
        assert(lightExecutedScriptArray != null) { "fromJson function return null value over an array" }
        val lightExecutedScript = lightExecutedScriptArray!!.toList()
        assert(lightExecutedScript.size == 2) { "fromJson function return an array with size ${lightExecutedScript.size} but expected size was 2" }

        val firstLightExecutedScript = lightExecutedScript[0]
        val secondLightExecutedScript = lightExecutedScript[1]

        assertAll(
            { assertEquals(firstLightExecutedScript._id, "3b0b62bc-4937-4757-9d49-25730492ce9f", "results[0]._id") },
            { assertEquals(firstLightExecutedScript.name, "1-test.js", "results[0].name") },
            { assertEquals(firstLightExecutedScript.checksum, "5247ebe6d2788ce6bcd4fc989c4528c8", "results[0].checksum") },
            { assertEquals(firstLightExecutedScript.identifier, "1", "results[0].identifier") },

            { assertEquals(secondLightExecutedScript._id, "7a4eaf36-ec96-4d3d-8c64-6b23b6e42290", "results[1]._id") },
            { assertEquals(secondLightExecutedScript.name, "2-test.js", "results[1].name") },
            { assertEquals(secondLightExecutedScript.checksum, "96188ec04746d6d213cdf553b9ddb138", "results[1].checksum") },
            { assertEquals(secondLightExecutedScript.identifier, "2", "results[1].identifier") },
        )
    }

    @Test
    fun fromJsonEmptyArray() {
        // Given
        val json = "[]"

        // When
        val lightExecutedScriptArray = jsonMapper.fromJson(json, Array<LightExecutedScriptDb>::class.java)

        //Then
        assert(lightExecutedScriptArray != null) { "fromJson function return null value over an array" }
        val lightExecutedScript = lightExecutedScriptArray!!.toList()
        assert(lightExecutedScript.isEmpty()) { "fromJson function return an array with size ${lightExecutedScript.size} but it was expected empty" }
    }

    private fun buildExecutedScriptDb() = ExecutedScriptDb(
        name = "name",
        checksum = "de0f0c582eff74ec1b67ac065078dc7e",
        identifier = "test",
        executionStatus = ExecutionStatus.OK,
        action = ScriptAction.RUN,
        executionDurationInMillis = 1L,
        executionOutput = "output",
        flags = listOf("flag1", "flag2")
    )

    private fun buildJsonLightExecutedScriptArray() = JSONArray()
        .appendElement(
            JSONObject()
                .appendField("_id", "3b0b62bc-4937-4757-9d49-25730492ce9f")
                .appendField("name", "1-test.js")
                .appendField("checksum", "5247ebe6d2788ce6bcd4fc989c4528c8")
                .appendField("identifier", "1")
        )
        .appendElement(
            JSONObject()
                .appendField("_id", "7a4eaf36-ec96-4d3d-8c64-6b23b6e42290")
                .appendField("name", "2-test.js")
                .appendField("checksum", "96188ec04746d6d213cdf553b9ddb138")
                .appendField("identifier", "2")
        )

    /**
     * Serializer can choose to not write empty list
     * Assert the path is empty or null
     */
    private fun DocumentContext.assertPathIsAnEmptyArrayOrNull(path: String) {
        val value = this.read<JSONArray>(path)
        assert(value == null || value.isEmpty()) { "Assertion fail for json path \"$path\". Array must be null or empty but was $value" }
    }

    /**
     * Read json path and assert value is equals
     * Print an error message if not
     */
    private fun <T : Any?> DocumentContext.assertPathEqual(path: String, expected: T) {
        val value = this.read<T>(path)
        assert(value == expected) { "Assertion fail for json path \"$path\" : expected $expected but was $value" }
    }

    private fun assertEquals(expected: Any?, value: Any?, propertyName: String) =
        assert(expected == value) { "Assertion fail for \"$propertyName\" : expected $expected but was $value" }

    /**
     * True if the property is writen
     */
    private fun jsonHasProperty(json: String, propertyName: String, path: String = "$") =
        JsonPath.parse(json).read<HashMap<String, String>>(path).containsKey(propertyName)
}
