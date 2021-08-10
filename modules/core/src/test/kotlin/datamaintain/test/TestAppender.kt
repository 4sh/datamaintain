package datamaintain.test

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase

class TestAppender : AppenderBase<ILoggingEvent>() {
    val events: MutableList<ILoggingEvent> = mutableListOf()

    override fun append(eventObject: ILoggingEvent) {
        events.add(eventObject)
    }

    fun clearEvents() {
        events.clear()
    }
}