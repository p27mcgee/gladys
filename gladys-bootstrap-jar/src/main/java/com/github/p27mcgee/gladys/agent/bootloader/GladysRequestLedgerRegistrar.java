package com.github.p27mcgee.gladys.agent.bootloader;

import java.lang.ref.WeakReference;

public class GladysRequestLedgerRegistrar {
	// This allows retrieving GladysRequestLedger from the running thread
	// even if ServletRequest is not available to the method logging the event.
	// WeakReference is used so that in case of an abnormal exit from a servlet
	// the GladysRequestLedger can still be garbage collected.
	// GladysRequestLedger is strongly referenced by a ServletRequest
	// attribute which prevents GC while the request is being processed.
	// We trust the servlet container to remove the ServletRequest attribute
	// regardless of how the Servlet.service call is terminated.
	
	static final ThreadLocal<WeakReference<GladysRequestLedger>> threadlocalGladysRequestLedger = 
			new ThreadLocal<WeakReference<GladysRequestLedger>>();

	public static GladysRequestLedger getGladysRequestLedgerFromThread() {
		GladysRequestLedger currentGladysRequestLogger = null; 
		WeakReference<GladysRequestLedger> grlRef = threadlocalGladysRequestLedger.get();
		if (grlRef != null) {
			currentGladysRequestLogger = grlRef.get(); 
		}
		return currentGladysRequestLogger;
	}

	public static void setGladysRequestLedgerInThread(GladysRequestLedger currentGladysRequestLogger) {
		threadlocalGladysRequestLedger.set(new WeakReference<GladysRequestLedger>(currentGladysRequestLogger));
	}

	public static void removeGladysRequestLedgerFromThread() {
		threadlocalGladysRequestLedger.remove();
	}

}
