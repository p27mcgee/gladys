package com.github.p27mcgee.gladys.agent.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.HttpAppender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.p27mcgee.gladys.agent.bootloader.GladysRequestLedger;

public class RemoteReportLedger {
	
	private static Logger logger = LogManager.getLogger(RemoteReportLedger.class);		

	static private ObjectMapper objectMapper = new ObjectMapper();

	public static void reportRemote(GladysRequestLedger requestEvents) {
		try {
			if (logger.isDebugEnabled()) {
				String requestEventsJson = objectMapper.writeValueAsString(requestEvents);
				logger.debug(requestEventsJson);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			logger.warn(e.getMessage(), e);
		}
	}
}
