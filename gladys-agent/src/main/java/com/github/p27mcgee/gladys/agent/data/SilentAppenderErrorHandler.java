package com.github.p27mcgee.gladys.agent.data;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.DefaultErrorHandler;

public class SilentAppenderErrorHandler extends DefaultErrorHandler {

	public SilentAppenderErrorHandler(Appender appender) {
		super(appender);
	}

	@Override
	public void error(String msg) {
		// Ignore
	}

	@Override
	public void error(String msg, Throwable t) {
		// Ignore
	}

	@Override
	public void error(String msg, LogEvent event, Throwable t) {
		// Ignore
	}
}
