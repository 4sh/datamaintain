package datamaintain.core.step.executor

import datamaintain.domain.script.ExecutionStatus

data class Execution (val executionStatus: ExecutionStatus, val executionOutput: String? = null)