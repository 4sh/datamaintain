package datamaintain.core.step.check.rules.implementations

import datamaintain.core.exception.DatamaintainCheckException
import datamaintain.core.step.check.rules.contracts.ScriptCheckRule
import datamaintain.domain.ScriptType
import datamaintain.domain.script.ScriptWithContent

class AlwaysFailedCheck: ScriptCheckRule() {
    override fun check(script: ScriptWithContent) {
        throw DatamaintainCheckException(getName(), "Use this rule for tests only")
    }

    override fun getName(): String {
        return NAME
    }

    override fun scriptType(): ScriptType {
        return ScriptType.SCANNED_SCRIPT
    }

    companion object {
        @JvmStatic
        val NAME = "AlwaysFailed"
    }
}
