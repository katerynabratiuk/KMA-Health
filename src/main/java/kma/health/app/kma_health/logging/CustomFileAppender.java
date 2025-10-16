package kma.health.app.kma_health.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class CustomFileAppender extends FileAppender<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {
        System.out.println("--- CUSTOM APPENDER: Writing event to file. Level: " + event.getLevel().levelStr);
        super.append(event);
    }

    @Override
    public void start() {
        if (getFile() == null) {
            addError("File property not set for the appender named [" + getName() + "].");
            return;
        }
        super.start();
    }
}
