package com.xck;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;

@Plugin(name = "LogFilterUser", category = Node.CATEGORY, elementType = Filter.ELEMENT_TYPE, printObject = true)
public class LogFilterUser extends AbstractFilter {

    public LogFilterUser() {
    }

    public LogFilterUser(Filter.Result onMatch, Filter.Result onMismatch) {
        super(onMatch, onMismatch);
    }

    @Override
    public Filter.Result filter(final org.apache.logging.log4j.core.Logger logger, final Level level, final Marker marker, final Object msg,
                                final Throwable t) {
        System.out.println("filter1");
        return Filter.Result.NEUTRAL;
    }

    @Override
    public Result filter(LogEvent event) {
        System.out.println("filter3");
        System.out.println(event.getMessage().getParameters()[0]);
        return super.filter(event);
    }

    @Override
    public Filter.Result filter(final org.apache.logging.log4j.core.Logger logger, final Level level, final Marker marker, final String msg,
                                final Object... params) {
        System.out.println("filter2");

        return Filter.Result.NEUTRAL;
    }

    @PluginFactory
    public static LogFilterUser createFilter(@PluginAttribute("onMatch") final Result match,
                                            @PluginAttribute("onMismatch") final Result mismatch,                                        @PluginAttribute("level") final Level level) {
        final Result onMatch = match == null ? Result.NEUTRAL : match;
        final Result onMismatch = mismatch == null ? Result.DENY : mismatch;
        return new LogFilterUser(onMatch, onMismatch);
    }
}
