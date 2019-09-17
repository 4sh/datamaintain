package datamaintain

import java.time.Instant

data class ExecutionReport(val date: Instant,
                           val status: String)