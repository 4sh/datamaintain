package datamaintain.core.exception

import datamaintain.core.report.Report
import datamaintain.core.report.ReportBuilder
import datamaintain.core.step.Step

class DatamaintainException(
    override val message: String,
    val step: Step,
    val report: Report,
    override val resolutionMessage: String = ""
) : DatamaintainBaseException(message) {
    constructor(
        message: String,
        step: Step,
        reportBuilder: ReportBuilder,
        resolutionMessage: String = ""
    ) : this(message, step, reportBuilder.toReport(), resolutionMessage)
}
