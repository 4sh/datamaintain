package datamaintain.core.step.check.rules.contracts

import datamaintain.core.exception.DatamaintainCheckException
import datamaintain.domain.CheckRule
import datamaintain.domain.script.ScriptWithContent

abstract class ScriptCheckRule: CheckRule {
    /**
     * @throws DatamaintainCheckException
     */
    @Throws(DatamaintainCheckException::class)
    abstract fun check(script: ScriptWithContent)
}
