package datamaintain.core.exception

import datamaintain.core.step.Step
import datamaintain.domain.report.Report
import datamaintain.domain.report.ReportBuilder

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
