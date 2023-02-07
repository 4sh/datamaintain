package datamaintain.test

import datamaintain.core.config.*
import datamaintain.core.db.driver.DatamaintainDriverConfig
import datamaintain.core.db.driver.FakeDriverConfig
import datamaintain.core.script.ScriptAction
import datamaintain.core.script.Tag
import datamaintain.core.script.TagMatcher
import datamaintain.core.step.executor.ExecutionMode
import java.nio.file.Path
import java.nio.file.Paths

fun buildDatamaintainConfig(
    path: Path = Paths.get(CoreConfigKey.SCAN_PATH.default!!),
    identifierRegex: Regex = Regex(CoreConfigKey.SCAN_IDENTIFIER_REGEX.default!!),
    doesCreateTagsFromFolder: Boolean = CoreConfigKey.CREATE_TAGS_FROM_FOLDER.default!!.toBoolean(),
    whitelistedTags: Set<Tag> = setOf(),
    blacklistedTags: Set<Tag> = setOf(),
    tagsToPlayAgain: Set<Tag> = setOf(),
    overrideExecutedScripts: Boolean = CoreConfigKey.PRUNE_OVERRIDE_UPDATED_SCRIPTS.default!!.toBoolean(),
    tagsMatchers: Set<TagMatcher> = setOf(),
    checkRules: Sequence<String> = emptySequence(),
    executionMode: ExecutionMode = ExecutionMode.NORMAL,
    defaultScriptAction: ScriptAction = DatamaintainExecutorConfig.defaultAction,
    driverConfig: DatamaintainDriverConfig = FakeDriverConfig(),
    verbose: Boolean = CoreConfigKey.VERBOSE.default!!.toBoolean(),
    porcelain: Boolean = CoreConfigKey.PRINT_RELATIVE_PATH_OF_SCRIPT.default!!.toBoolean()
) = DatamaintainConfig(
    scanner = DatamaintainScannerConfig(
        path = path,
        identifierRegex = identifierRegex,
        doesCreateTagsFromFolder = doesCreateTagsFromFolder,
        tagsMatchers = tagsMatchers,
    ),
    filter = DatamaintainFilterConfig(
        whitelistedTags = whitelistedTags,
        blacklistedTags = blacklistedTags,
    ),
    pruner = DatamaintainPrunerConfig(
        tagsToPlayAgain = tagsToPlayAgain
    ),
    checker = DatamaintainCheckerConfig(
        rules = checkRules.toList(),
    ),
    executor = DatamaintainExecutorConfig(
        executionMode = executionMode,
        defaultScriptAction = defaultScriptAction,
        overrideExecutedScripts = overrideExecutedScripts
    ),
    driverConfig = driverConfig,
    logs = DatamaintainLogsConfig(
        verbose = verbose,
        porcelain = porcelain
    )
)
