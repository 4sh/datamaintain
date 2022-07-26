package datamaintain.monitoring

import MonitoringReport
import com.fasterxml.jackson.databind.ObjectMapper
import datamaintain.domain.report.IReportSender
import datamaintain.domain.report.Report
import org.http4k.client.Java8HttpClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.format.ConfigurableJackson

val url = "http://localhost:8080/api/public/reports"

class ReportSender : IReportSender {
    private val httpClient = Java8HttpClient()
    override fun sendReport(report: Report) {
        httpClient(Request(Method.POST, url).body(ConfigurableJackson(ObjectMapper()).asFormatString(report.toMonitoringReport())))
    }
}

fun Report.toMonitoringReport() =
    MonitoringReport(this.executedScripts.size)