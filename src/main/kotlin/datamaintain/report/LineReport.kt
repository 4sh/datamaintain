package datamaintain.report

import java.time.Instant

interface LineReport {
    val date: Instant
    val message: String
    val level: LineReportLevel
}