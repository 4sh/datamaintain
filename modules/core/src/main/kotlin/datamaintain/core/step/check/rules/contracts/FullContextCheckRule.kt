package datamaintain.core.step.check.rules.contracts

import datamaintain.core.exception.DatamaintainCheckException
import datamaintain.domain.CheckRule
import datamaintain.domain.script.LightExecutedScript
import datamaintain.domain.script.ScriptWithContent

abstract class FullContextCheckRule(
        val executedScripts: Sequence<LightExecutedScript>
): CheckRule {
    /**
     * @throws DatamaintainCheckException
     */
    @Throws(DatamaintainCheckException::class)
    abstract fun check(scripts: Sequence<ScriptWithContent>)
}
