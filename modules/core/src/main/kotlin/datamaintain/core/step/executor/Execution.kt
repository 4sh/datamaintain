package datamaintain.core.step.executor

import datamaintain.core.script.ExecutionStatus

data class Execution (val executionStatus: ExecutionStatus, val executionOutput: String? = null)