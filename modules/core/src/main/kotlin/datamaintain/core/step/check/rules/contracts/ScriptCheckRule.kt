package datamaintain.core.step.check.rules.contracts

import datamaintain.core.script.ScriptWithContent
import datamaintain.core.step.check.rules.CheckRule

abstract class ScriptCheckRule: CheckRule {
    abstract fun check(script: ScriptWithContent)
}