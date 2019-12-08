package datamaintain.core.report

import java.time.Instant

interface Report {
    val date: Instant
    val status: ReportStatus
    val lines: List<LineReport>
}