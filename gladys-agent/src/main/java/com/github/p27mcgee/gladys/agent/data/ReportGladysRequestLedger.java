package com.github.p27mcgee.gladys.agent.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.p27mcgee.gladys.agent.bootloader.GladysRequestLedger;

public class ReportGladysRequestLedger {
	
	private static Logger logger = LogManager.getLogger(ReportGladysRequestLedger.class);		

    public static void report(GladysRequestLedger gladysRequestLedger) {
    	reportSummary(gladysRequestLedger);		
    	RemoteReportLedger.reportRemote(gladysRequestLedger);
	}

	private static void reportSummary(GladysRequestLedger gladysRequestLedger) {
		logger.info(gladysRequestLedger.createSummary());
	}
}
