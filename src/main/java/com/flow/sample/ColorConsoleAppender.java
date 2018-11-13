package com.flow.sample;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.spi.LoggingEvent;

public class ColorConsoleAppender extends ConsoleAppender {

    @Override
    protected void subAppend(final LoggingEvent event) {
        int color = 30;
        if (event.getRenderedMessage().contains(MagazineSubscriber.JACK)) {
            color = 34;
        } else if (event.getRenderedMessage().contains(MagazineSubscriber.PETE)) {
            color = 31;
        }
        qw.write("\u001b[0;" + color + "m");
        super.subAppend(event);
        qw.write("\u001b[m");
        if (this.immediateFlush) qw.flush();
    }

}
