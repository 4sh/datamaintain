package datamaintain.core.step.check.rules.contracts

import datamaintain.core.exception.DatamaintainCheckException
import datamaintain.core.script.ScriptWithContent
import datamaintain.core.step.check.rules.CheckRule

abstract class ScriptCheckRule: CheckRule {
    /**
     * @throws DatamaintainCheckException
     */
    @Throws(DatamaintainCheckException::class)
    abstract fun check(script: ScriptWithContent)
}
