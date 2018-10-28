package com.github.p27mcgee.gladys.agent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.p27mcgee.gladys.agent.bootloader.GladysRequestLedger;
import com.github.p27mcgee.gladys.agent.bootloader.GladysRequestLedgerRegistrar;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

public class InstrumentedBuilderListener implements AgentBuilder.Listener{
	private static Logger logger = LogManager.getLogger(InstrumentedBuilderListener.class);	
	
	public InstrumentedBuilderListener() {
	}
	
    @Override
    public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
    	if (logger.isTraceEnabled()) {
    		logger.trace("completed: " + typeName);
    	}
    	
    	GladysRequestLedger gladyRequestLogger = 
    			GladysRequestLedgerRegistrar.getGladysRequestLedgerFromThread();    			   	
    	if (gladyRequestLogger != null) {
    		gladyRequestLogger.logClassLoaded(typeName);
    	}

    	if (logger.isDebugEnabled()) {
        	logger.debug("completed in request processing: " + typeName);
    	}
    }
    
    @Override
    public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
    	if (logger.isTraceEnabled()) {
    		logger.trace("discovered: " + typeName);
    	}
    }
    
    @Override
    public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
    	logger.warn("errored: " + typeName);
    	logger.warn("error === " + throwable.getMessage(), throwable);
    }
    
    @Override
    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
    	if (logger.isTraceEnabled()) {
    		logger.trace("ignored: " + typeDescription);
    	}
    }
    
    @Override
    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
    	if (logger.isDebugEnabled()) {
    		logger.debug("transformed: " + typeDescription + ", type = " + dynamicType);
    	}
    }

}
