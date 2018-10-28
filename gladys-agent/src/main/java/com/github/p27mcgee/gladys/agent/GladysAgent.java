package com.github.p27mcgee.gladys.agent;

import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.isAbstract;
import static net.bytebuddy.matcher.ElementMatchers.isConstructor;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;
import static net.bytebuddy.matcher.ElementMatchers.not;

import java.lang.instrument.Instrumentation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.p27mcgee.gladys.agent.advice.HttpServletAdvice;
import com.github.p27mcgee.gladys.agent.advice.StringConstructorAdvice;
import com.github.p27mcgee.gladys.agent.bootloader.GladysRequestLedger;
import com.github.p27mcgee.gladys.agent.data.GladysRequestLedgerImpl;

import net.bytebuddy.agent.builder.AgentBuilder;

public class GladysAgent {
	
	private static Logger logger = LogManager.getLogger(GladysAgent.class);	

	static private String agentArgs;
	static private Instrumentation instrumentation;

	static {
		ClassLoaderInfoLogger.logClassLoaderInfo(GladysAgent.class);
	}

	public static void premain(String agentArgs, Instrumentation instrumentation) {
		GladysAgent.agentArgs = agentArgs;
		GladysAgent.instrumentation = instrumentation;

		logger.info("GladysAgent is instrumenting the application.");
		logger.info("agentArgs = " + agentArgs);
				
/*		
	 The class loading of Java web applications	and the packaging of 
	 classes by Spring Boot
 		https://groups.google.com/forum/#!topic/byte-buddy/lkAvpB1NyME 
	 seem to make this approach problematic:
		
		new AgentBuilder.Default()
			.type(hasSuperType(named("javax.servlet.http.HttpServlet")))  
        	.transform(new AgentBuilder.Transformer() {
        		@Override
 				public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription,
						ClassLoader classLoader, JavaModule module) {
					return builder.method(named("service"))
						.intercept(MethodDelegation.to(HttpServletInterceptor.class));
				}
        	}).installOn(instrumentation);		
*/		
		
		// Rafael recommends using "Advice" rather than MethodDelegation
		// so that the advice code is loaded into the Servlet's context 
		// classloader that allows it to access ServletRequest,  ServletResponse
		// and Session objects
		new AgentBuilder.Default()
		.with(new InstrumentedBuilderListener())
		.with(new DebugBuilderListener())
		.type(hasSuperType(named("javax.servlet.Servlet")))
        .transform(
            new AgentBuilder.Transformer.ForAdvice()
               .include(HttpServletAdvice.class.getClassLoader())
               .advice(named("service").and(not(isAbstract())), 
            		  HttpServletAdvice.class.getName())
        )
        .installOn(instrumentation);								
		
		logger.info("GladysAgent has applied HttpServlet advice.");
		

		new AgentBuilder.Default()
		.with(new DebugBuilderListener())
        .ignore(none())
        .disableClassFormatChanges()
        .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
        .type(named("java.lang.String"))
        .transform( 
   	        new AgentBuilder.Transformer.ForAdvice()
    	        .include(StringConstructorAdvice.class.getClassLoader())
    	        .advice(isConstructor(), StringConstructorAdvice.class.getName())
		)
        .installOn(instrumentation); 

		logger.info("GladysAgent has applied String constructor advice for String creation counting.");

//		String foo = "BOO!";
//		long size = instrumentation.getObjectSize(foo);
//System.out.println("size of " + foo + " is " + size);	
		// This works: "size of BOO! is 24
		
// this breaks the app!!!		
//		new AgentBuilder.Default()
//		.with(new DebugBuilderListener())
//        .ignore(none())
//        .disableClassFormatChanges()
//        .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
//        .type(named("java.lang.Object"))
//        .transform( 
//	        new AgentBuilder.Transformer.ForAdvice()
//    	        .include(ObjectConstructorAdvice.class.getClassLoader())
//    	        .advice(isConstructor(), ObjectConstructorAdvice.class.getName())
//		)
//        .installOn(instrumentation); 

//		logger.info("GladysAgent has applied Object constructor advice for heap usage monitoring.");

		// ObjectConstructorAdvice should catch heap allocated for 
		// all objects descendant from java.lang.Object and created with "new"
		
		// TODO Heap usage for the following is not be included in the totals:
		// - Array objects
		// - Object created by reflection, e.g. Class.newInstance();
		// - Objects created by Object.clone()
		// - Objects created by deserialization		
		// others?

	}
	
	public static String getAgentArgs() {
		return agentArgs;
	}

	public static void setAgentArgs(String agentArgs) {
		GladysAgent.agentArgs = agentArgs;
	}


	public static Instrumentation getInstrumentation() {
		return instrumentation;
	}

	public static void setInstrumentation(Instrumentation instrumentation) {
		GladysAgent.instrumentation = instrumentation;
	}
	
	public static GladysRequestLedger createGladysRequestLedger() {
		return new GladysRequestLedgerImpl(instrumentation);
	}
}
