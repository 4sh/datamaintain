package datamaintain.core.step.check.rules.contracts

import datamaintain.core.exception.DatamaintainCheckException
import datamaintain.domain.CheckRule
import datamaintain.domain.script.ExecutedScript
import datamaintain.domain.script.ScriptWithContent

abstract class ScriptWithContextCheckRule(
        executedScripts: Sequence<ExecutedScript>
): CheckRule {
    /**
     * @throws DatamaintainCheckException
     */
    @Throws(DatamaintainCheckException::class)
    abstract fun check(script: ScriptWithContent)
}
