package com.github.p27mcgee.gladys.agent.bootloader;

public interface GladysRequestLedger {

	void enteringServlet(String servletName);

	void setHttpMethod(String method);

	void setUrl(String string);

	void setSessionId(String id);
	
	void exitingServlet(String servletName);
	
	
	boolean isComplete();

	void logStringCreate();

	void logClassLoaded(String fqClassName);

	void logHeapAllocation(Object createdObject);

	
	long getDurationNanos();

	int getClassLoadCount();

	long getMemoryUsage();

	int getStringCreateCount();

	long getStartNanos();

	String getUniqueId();

	String getSessionId();
	
	String getHttpMethod();

	String getUrl();
	
	String createSummary();	
}