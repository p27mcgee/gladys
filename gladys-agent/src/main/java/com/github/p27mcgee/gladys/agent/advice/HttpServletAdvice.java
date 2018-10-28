package com.github.p27mcgee.gladys.agent.advice;

import java.lang.reflect.Method;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.github.p27mcgee.gladys.agent.GladysAgent;
import com.github.p27mcgee.gladys.agent.bootloader.GladysRequestLedger;
import com.github.p27mcgee.gladys.agent.bootloader.GladysRequestLedgerRegistrar;
import com.github.p27mcgee.gladys.agent.data.ReportGladysRequestLedger;

import net.bytebuddy.asm.Advice;

// the advice code is injected into the Servlet.service method
// in the Web Application context class loader.
// the Agent classes are loaded in the System class
// loader and will be available to the classes in the
// context class loader(s)
public class HttpServletAdvice {

    @Advice.OnMethodEnter
    public static void before(@Advice.This Servlet servletInstance,
            @Advice.Argument(value = 0) ServletRequest req,
            @Advice.Argument(value = 1) ServletResponse resp,
            @Advice.Origin Method method) {

    	// Create or retrieve RequestEvents for this request  
    	GladysRequestLedger gladysRequestLedger = (GladysRequestLedger) 
    			req.getAttribute(GladysRequestLedger.class.getName());    	
     	if (gladysRequestLedger == null) {     		
      		gladysRequestLedger = GladysAgent.createGladysRequestLedger();
        	req.setAttribute(GladysRequestLedger.class.getName(), gladysRequestLedger);
    	} 

    	// record entry into this servlet  
     	gladysRequestLedger.enteringServlet(servletInstance.getClass().getName());
    	
     	GladysRequestLedgerRegistrar.setGladysRequestLedgerInThread(gladysRequestLedger);
    	
    	// init requestEvents
		if (req instanceof HttpServletRequest) {
			HttpServletRequest httpReq = (HttpServletRequest)req;
			gladysRequestLedger.setHttpMethod(httpReq.getMethod());
			gladysRequestLedger.setUrl(httpReq.getRequestURL().toString());
			HttpSession sess = httpReq.getSession(false);
			if (sess != null) {
				gladysRequestLedger.setSessionId(sess.getId());	
			}
		}
	}
    
    @Advice.OnMethodExit
    public static void after(@Advice.This Servlet servletInstance,
            @Advice.Argument(value = 0) ServletRequest req,
            @Advice.Argument(value = 1) ServletResponse resp,
            @Advice.Origin Method method) {
		
    	GladysRequestLedger gladysRequestLogger = (GladysRequestLedger)
    			req.getAttribute(GladysRequestLedger.class.getName());

    	// remove record of this servlet entry  
    	gladysRequestLogger.exitingServlet(servletInstance.getClass().getName());
    	if (gladysRequestLogger.isComplete()) {
    		// when each servlet entry has had a matching servlet exit
    		GladysRequestLedgerRegistrar.removeGladysRequestLedgerFromThread();    	
	    	ReportGladysRequestLedger.report(gladysRequestLogger);		
    	}
	}
}
