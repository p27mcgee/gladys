package com.github.p27mcgee.gladys.agent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.p27mcgee.gladys.agent.data.ReportGladysRequestLedger;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

public class DebugBuilderListener implements AgentBuilder.Listener{
	private static Logger logger = LogManager.getLogger(DebugBuilderListener.class);	
	
	public DebugBuilderListener() {
	}
	
    @Override
    public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
    	logger.trace("completed: " + typeName);              
		logger.trace("Thread[" + Thread.currentThread().getName() + " ] loads class:\n\t " + typeName);  			
    }
    
    @Override
    public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
    	logger.trace("discovered: " + typeName);              
    }
    
    @Override
    public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
    	logger.warn("errored: " + typeName);
    	logger.warn("error === " + throwable.getMessage(), throwable);
     }
    
    @Override
    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
    	logger.trace("ignored: " + typeDescription);
    }
    
    @Override
    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
        logger.debug("transformed: " + typeDescription + ", type = " + dynamicType);
    }

}
