package com.github.p27mcgee.gladys.agent.data;

import java.io.Serializable;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.p27mcgee.gladys.agent.ClassLoaderInfoLogger;
import com.github.p27mcgee.gladys.agent.bootloader.GladysRequestLedger;

public class GladysRequestLedgerImpl implements Serializable, GladysRequestLedger {

	private static final long serialVersionUID = 1L;

	public static final long DURATION_INCOMPLETE = -1;
	public static final long INVALID_MEMORY_USAGE  = -1;
	public static final boolean RECORD_LOADED_CLASSES = false;
	static final double NANOS_PER_MILLI = 1000000.0f;

	static {
		ClassLoaderInfoLogger.logClassLoaderInfo(GladysRequestLedgerImpl.class);
	}

	private Instrumentation instrumentation;	
	
	String uniqueId;
	String threadName;

	String httpMethod;
	String url;
	String sessionId;
	
	long startNanos = 0;
	long endNanos = -1;

	int stringCreateCount = 0;
	long memoryUsage = 0;
	int classLoadCount = 0;

	// A single HttpRequest might be forwarded to 
	// multiple servlets during processing.
	// The processing is done when every servlet
	// entered by the request/thread has also 
	// been exited.  servletStack keeps the list
	// of servlets entered and not yet exited.
	Stack<String> servletStack = new Stack<>(); 

	String instrumentationExceptionMessage;
	
	@JsonIgnore
	Throwable instrumentationException = null;
	
	List<String> loadedClasses = RECORD_LOADED_CLASSES ? new ArrayList<>() : null;
	
	private GladysRequestLedgerImpl() {		
		threadName = Thread.currentThread().getName();			
		uniqueId = UUID.randomUUID().toString();
		startNanos = System.nanoTime();
	}

	public GladysRequestLedgerImpl(Instrumentation instrumentation) {
		this();
		this.instrumentation = instrumentation;
	}
	
	@Override
	public boolean isComplete() {
		return DURATION_INCOMPLETE != getDurationNanos();
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String method) {
		this.httpMethod = method;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String getUniqueId() {
		return uniqueId;
	}

	@Override
	public long getStartNanos() {
		return startNanos;
	}

	public long getEndNanos() {
		return endNanos;
	}

	@Override
	public int getStringCreateCount() {
		return stringCreateCount;
	}

	@Override
	public long getMemoryUsage() {
		return memoryUsage;
	}

	@Override
	public int getClassLoadCount() {
		return classLoadCount;
	}

	public String getInstrumentationExceptionMessage() {
		return instrumentationExceptionMessage;
	}

	@JsonIgnore
	public List<String> getLoadedClasses() {
		return loadedClasses;
	}
	
	////////////////////////////////////////////////////////////////////

	@Override
	public void enteringServlet(String servletName) {
		servletStack.push(servletName);
	}
	
	/* (non-Javadoc)
	 * @see com.github.p27mcgee.gladys.agent.data.GladysRequestLogger#logCompletion(java.lang.String)
	 */
	@Override
	public void exitingServlet(String servletName) {
		if (servletName.equals(servletStack.peek())) {
			servletStack.pop();
			if (servletStack.isEmpty()) {
				endNanos = System.nanoTime();
			}
		} else {
			instrumentationExceptionMessage = 
				"logCompletion(" + servletName + ") called "
					+ "with servletStack: " + servletStack;
			instrumentationException = 
				new IllegalStateException(instrumentationExceptionMessage);
			endNanos = System.nanoTime();
		}
	}

	/* (non-Javadoc)
	 * @see com.github.p27mcgee.gladys.agent.data.GladysRequestLogger#logStringCreate()
	 */
	@Override
	public void logStringCreate() {
		stringCreateCount++;
	}

	/* (non-Javadoc)
	 * @see com.github.p27mcgee.gladys.agent.data.GladysRequestLogger#logClassLoaded(java.lang.String)
	 */
	@Override
	public void logClassLoaded(String fqClassName) {
		++classLoadCount;
		if (RECORD_LOADED_CLASSES) {
			loadedClasses.add(fqClassName);
		}
	}

	/* (non-Javadoc)
	 * @see com.github.p27mcgee.gladys.agent.data.GladysRequestLogger#logHeapAllocation(int)
	 */
	@Override
	public void logHeapAllocation(Object createdObject) {
		if (instrumentation != null) {
			if (createdObject != null) {
				long objSize = instrumentation.getObjectSize(createdObject);	
				memoryUsage += objSize;
			}			
		} else {
			memoryUsage = INVALID_MEMORY_USAGE;
		}
	}
	
	@Override
	public long getDurationNanos() {
		if (endNanos >= startNanos) {
			return endNanos - startNanos;
		} else {
			return DURATION_INCOMPLETE;
		}
	}

	@Override
	public String createSummary() {
		String summary;
		if (isComplete()) {
			double mSec = getDurationNanos() / NANOS_PER_MILLI;
			summary = String.format("%,10.3f mSec to %s %s | requestId: %s | %6d Strings created | %4d Classes loaded", 
					mSec,
					getHttpMethod(),
					getUrl(),
					getUniqueId(),
					getStringCreateCount(),
					getClassLoadCount()
					);
		} else {
			summary = String.format("Incomplete request started at %,14d nSec %s %s | requestId: %s | %6d Strings created | %4d Classes loaded", 
					getStartNanos(),
					getHttpMethod(),
					getUrl(),
					getUniqueId(),
					getStringCreateCount(),
					getClassLoadCount()
					);
		}
		
		return summary;
	}
	
}
	
	