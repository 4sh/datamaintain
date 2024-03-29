package datamaintain.core.step.check.rules.contracts

import datamaintain.core.exception.DatamaintainCheckException
import datamaintain.core.script.LightExecutedScript
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.step.check.rules.CheckRule

abstract class FullContextCheckRule(
        val executedScripts: Sequence<LightExecutedScript>
): CheckRule {
    /**
     * @throws DatamaintainCheckException
     */
    @Throws(DatamaintainCheckException::class)
    abstract fun check(scripts: Sequence<ScriptWithContent>)
}
