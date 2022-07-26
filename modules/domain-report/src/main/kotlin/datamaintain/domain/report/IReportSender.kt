package datamaintain.domain.report

interface IReportSender {
    fun sendReport(report: Report)
}